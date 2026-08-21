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
import io.ktor.client.statement.body
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
        client.get("$baseUrl/health") { auth() }.body<HealthStatus>()

    override suspend fun getSessions(): List<SessionSummary> =
        client.get("$baseUrl/api/sessions") { auth() }.body<List<SessionSummary>>()

    override fun postChat(sessionId: String, request: ChatRequest): Flow<StreamEvent> = flow {
        val response: HttpResponse = client.post("$baseUrl/api/sessions/$sessionId/chat?stream=true") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(HermesJson.encodeToString(ChatRequest.serializer(), request))
        }
        deliverEvents(response)
    }

    override fun getRunEvents(runId: String): Flow<StreamEvent> = flow {
        val response: HttpResponse = client.get("$baseUrl/v1/runs/$runId/events") { auth() }
        deliverEvents(response)
    }

    override suspend fun postApproval(runId: String, decision: String, scope: String?): ApprovalResult =
        client.post("$baseUrl/v1/runs/$runId/approval") {
            auth()
            contentType(ContentType.Application.Json)
            setBody(ApprovalDecision(decision = decision, scope = scope))
        }.body<ApprovalResult>()

    override suspend fun stopRun(runId: String) {
        client.post("$baseUrl/v1/runs/$runId/stop") { auth() }.body<String>()
    }

    private suspend fun deliverEvents(response: HttpResponse) {
        val raw: String = response.body<String>()
        parseSseText(raw) { event, data -> emit(parseStreamEvent(event, data)) }
    }

    private suspend fun parseSseText(raw: String, emit: suspend (String, String) -> Unit) {
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
                        emit(event, data.toString())
                        event = "message"
                        data.clear()
                    }
                }
            }
        }
        if (data.isNotEmpty()) emit(event, data.toString())
    }
}
