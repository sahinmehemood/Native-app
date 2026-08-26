package com.hermes.android.core.gateway

import com.hermes.android.core.gateway.model.ApprovalDecision
import com.hermes.android.core.gateway.model.ApprovalResult
import com.hermes.android.core.gateway.model.Capabilities
import com.hermes.android.core.gateway.model.ChatRequest
import com.hermes.android.core.gateway.model.HealthStatus
import com.hermes.android.core.gateway.model.Message
import com.hermes.android.core.gateway.model.SessionSummary
import com.hermes.android.core.gateway.model.StreamEvent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * Ktor implementation of [HermesGatewayClient] targeting the stable
 * `api_server` REST + SSE surface (docs/HERMES-MOBILE-API.md §1).
 *
 * SSE frames are read from the raw response body and parsed with
 * [parseStreamEvent]; we intentionally avoid the bespoke relay transport.
 *
 * The client is **config-hot**: it reads the active [GatewayConfig] from
 * [configProvider] on every request, so a Settings change (host / API key)
 * takes effect immediately without rebuilding the Koin graph (see
 * [com.hermes.android.feature.settings.SettingsViewModel]).
 */
class KtorHermesGatewayClient(
    private val configProvider: GatewayConfigProvider,
) : HermesGatewayClient {

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) { json(HermesJson) }
        expectSuccess = true
    }

    private val config get() = configProvider.current()

    private fun HttpRequestBuilder.auth() {
        header(HttpHeaders.Authorization, "Bearer ${config.apiKey}")
    }

    override suspend fun getHealth(): HealthStatus =
        HermesJson.decodeFromString<HealthStatus>(client.get("${config.baseUrl}/health") { auth() }.bodyAsText())

    override suspend fun getCapabilities(): Capabilities =
        HermesJson.decodeFromString<Capabilities>(client.get("${config.baseUrl}/v1/capabilities") { auth() }.bodyAsText())

    override suspend fun getSessions(): List<SessionSummary> =
        decodeList(client.get("${config.baseUrl}/api/sessions") { auth() }.bodyAsText())

    override suspend fun createSession(): SessionSummary =
        HermesJson.decodeFromString<SessionSummary>(
            client.post("${config.baseUrl}/api/sessions") {
                auth()
                contentType(ContentType.Application.Json)
                setBody("{}")
            }.bodyAsText(),
        )

    override suspend fun getSessionMessages(sessionId: String): List<Message> =
        decodeList(client.get("${config.baseUrl}/api/sessions/$sessionId/messages") { auth() }.bodyAsText())

    override suspend fun deleteSession(sessionId: String) {
        client.delete("${config.baseUrl}/api/sessions/$sessionId") { auth() }.bodyAsText()
    }

    override fun postChat(sessionId: String, request: ChatRequest): Flow<StreamEvent> = flow {
        val response: HttpResponse = client.post("${config.baseUrl}/api/sessions/$sessionId/chat?stream=true") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(HermesJson.encodeToString(request))
        }
        emitSse(response.bodyAsText())
    }

    override fun getRunEvents(runId: String): Flow<StreamEvent> = flow {
        val response: HttpResponse = client.get("${config.baseUrl}/v1/runs/$runId/events") { auth() }
        emitSse(response.bodyAsText())
    }

    override suspend fun postApproval(runId: String, decision: String, scope: String?): ApprovalResult =
        HermesJson.decodeFromString(
            client.post("${config.baseUrl}/v1/runs/$runId/approval") {
                auth()
                contentType(ContentType.Application.Json)
                setBody(ApprovalDecision(decision = decision, scope = scope))
            }.bodyAsText(),
        )

    override suspend fun stopRun(runId: String) {
        client.post("${config.baseUrl}/v1/runs/$runId/stop") { auth() }.bodyAsText()
    }

    /**
     * Decode a list response that may be either a bare JSON array or the
     * OpenAI-style `{ "object": "list", "data": [ ... ] }` envelope the gateway
     * actually emits. Tolerant of either shape so the client never breaks if the
     * envelope is (or isn't) present (contract §7).
     */
    private inline fun <reified T> decodeList(raw: String): List<T> {
        val element: JsonElement = HermesJson.parseToJsonElement(raw)
        val array: JsonArray = when (element) {
            is JsonArray -> element
            is JsonObject -> element["data"] as? JsonArray
            else -> null
        } ?: JsonArray(emptyList())
        return HermesJson.decodeFromString<List<T>>(array.toString())
    }

    /** Emit each parsed SSE frame into this flow. Runs inside flow {} so emit is native. */
    private suspend fun kotlinx.coroutines.flow.FlowCollector<StreamEvent>.emitSse(raw: String) {
        var event = "message"
        val data = StringBuilder()
        for (line in raw.lineSequence()) {
            when {
                line.startsWith("event:") -> event = line.removePrefix("event:").trim()
                line.startsWith("data:") -> {
                    if (data.isNotEmpty()) data.append('\n')
                    data.append(line.removePrefix("data:").trim())
                }
                line.isEmpty() -> {
                    if (data.isNotEmpty()) {
                        emit(parseStreamEvent(event, data.toString()))
                        event = "message"
                        data.clear()
                    }
                }
            }
        }
        if (data.isNotEmpty()) emit(parseStreamEvent(event, data.toString()))
    }
}
