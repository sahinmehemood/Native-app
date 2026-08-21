package com.hermes.android.core.gateway

import com.hermes.android.core.gateway.model.ApprovalDecision
import com.hermes.android.core.gateway.model.ApprovalResult
import com.hermes.android.core.gateway.model.ChatRequest
import com.hermes.android.core.gateway.model.HealthStatus
import com.hermes.android.core.gateway.model.SessionSummary
import com.hermes.android.core.gateway.model.StreamEvent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
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

/**
 * Ktor implementation of [HermesGatewayClient] targeting the stable
 * `api_server` REST + SSE surface (docs/HERMES-MOBILE-API.md §1).
 *
 * SSE frames are read from the raw response body and parsed with
 * [parseStreamEvent]; we intentionally avoid the bespoke relay transport.
 */
class KtorHermesGatewayClient(
    private val baseUrl: String,
    private val apiKey: String,
) : HermesGatewayClient {

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) { json(HermesJson) }
        expectSuccess = true
    }

    private fun HttpRequestBuilder.auth() {
        header(HttpHeaders.Authorization, "Bearer $apiKey")
    }

    override suspend fun getHealth(): HealthStatus =
        HermesJson.decodeFromString<HealthStatus>(client.get("$baseUrl/health") { auth() }.bodyAsText())

    override suspend fun getSessions(): List<SessionSummary> =
        HermesJson.decodeFromString(client.get("$baseUrl/api/sessions") { auth() }.bodyAsText())

    override fun postChat(sessionId: String, request: ChatRequest): Flow<StreamEvent> = flow {
        val response: HttpResponse = client.post("$baseUrl/api/sessions/$sessionId/chat?stream=true") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(HermesJson.encodeToString(request))
        }
        emitSse(response.bodyAsText())
    }

    override fun getRunEvents(runId: String): Flow<StreamEvent> = flow {
        val response: HttpResponse = client.get("$baseUrl/v1/runs/$runId/events") { auth() }
        emitSse(response.bodyAsText())
    }

    override suspend fun postApproval(runId: String, decision: String, scope: String?): ApprovalResult =
        HermesJson.decodeFromString(
            client.post("$baseUrl/v1/runs/$runId/approval") {
                auth()
                contentType(ContentType.Application.Json)
                setBody(ApprovalDecision(decision = decision, scope = scope))
            }.bodyAsText(),
        )

    override suspend fun stopRun(runId: String) {
        client.post("$baseUrl/v1/runs/$runId/stop") { auth() }.bodyAsText()
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
