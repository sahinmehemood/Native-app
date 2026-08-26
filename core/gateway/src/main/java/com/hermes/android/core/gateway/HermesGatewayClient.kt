package com.hermes.android.core.gateway

import com.hermes.android.core.gateway.model.ApprovalResult
import com.hermes.android.core.gateway.model.Capabilities
import com.hermes.android.core.gateway.model.ChatRequest
import com.hermes.android.core.gateway.model.HealthStatus
import com.hermes.android.core.gateway.model.Message
import com.hermes.android.core.gateway.model.SessionSummary
import com.hermes.android.core.gateway.model.StreamEvent
import kotlinx.coroutines.flow.Flow

/**
 * Connection status surfaced to the UI. Shared by Home and Chat (neither feature
 * depends on the other, so the enum lives in core:gateway).
 */
enum class ConnectionStatus { Connected, Reconnecting, Offline }

/**
 * Client contract for the Hermes `api_server` surface (docs/HERMES-MOBILE-API.md).
 *
 * Implemented by [KtorHermesGatewayClient]; safe to fake in unit tests so the
 * streaming state machine and reconnect rules can be verified without the network.
 */
interface HermesGatewayClient {
    suspend fun getHealth(): HealthStatus
    suspend fun getSessions(): List<SessionSummary>
    suspend fun createSession(): SessionSummary
    suspend fun getSessionMessages(sessionId: String): List<Message>
    suspend fun deleteSession(sessionId: String)
    suspend fun getCapabilities(): Capabilities
    fun postChat(sessionId: String, request: ChatRequest): Flow<StreamEvent>
    fun getRunEvents(runId: String): Flow<StreamEvent>
    suspend fun postApproval(runId: String, decision: String, scope: String? = null): ApprovalResult
    suspend fun stopRun(runId: String)
}

/**
 * Connection profile for a single gateway. Provided by the app shell (Android
 * Keystore + DataStore); never embedded in the APK (AGENTS.md security boundary).
 */
data class GatewayConfig(val baseUrl: String, val apiKey: String)

/**
 * Parse one raw SSE frame (`event:` + `data:` lines) into a [StreamEvent].
 *
 * The `data` payload is exposed as a [StreamEvent.data] JsonElement so the
 * consumer can deserialize only the fields it understands — unknown events and
 * unknown fields are tolerated per the contract (§7).
 */
fun parseStreamEvent(event: String, data: String): StreamEvent =
    StreamEvent(
        event = if (event.isBlank()) "message" else event,
        data = HermesJson.parseToJsonElement(if (data.isBlank()) "{}" else data),
    )
