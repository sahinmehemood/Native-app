package com.hermes.android.core.data

import com.hermes.android.core.gateway.HermesGatewayClient
import com.hermes.android.core.gateway.model.Capabilities
import com.hermes.android.core.gateway.model.ChatRequest
import com.hermes.android.core.gateway.model.HealthStatus
import com.hermes.android.core.gateway.model.Message
import com.hermes.android.core.gateway.model.SessionSummary
import kotlinx.coroutines.flow.collect

/**
 * Thin domain wrapper over [HermesGatewayClient] for the Home/Settings/sessions
 * surfaces.
 *
 * Centralizing the call here keeps feature ViewModels free of transport
 * concerns and gives us one place to add caching/retry later without changing
 * the contract (docs/HERMES-MOBILE-API.md §3).
 */
class SessionRepository(private val gateway: HermesGatewayClient) {
    suspend fun getSessions(): List<SessionSummary> = gateway.getSessions()
    suspend fun createSession(): SessionSummary = gateway.createSession()
    suspend fun getSessionMessages(sessionId: String): List<Message> = gateway.getSessionMessages(sessionId)
    suspend fun deleteSession(sessionId: String) = gateway.deleteSession(sessionId)
    suspend fun getCapabilities(): Capabilities = gateway.getCapabilities()
    suspend fun getHealth(): HealthStatus = gateway.getHealth()

    /**
     * Dispatch a single user message to a session and consume the stream to
     * completion. Used by NOUS capture and Automations run — the response is
     * persisted server-side, so the client only needs to confirm delivery.
     */
    suspend fun postChatMessage(sessionId: String, message: String) {
        gateway.postChat(sessionId, ChatRequest(message = message, stream = true)).collect { }
    }
