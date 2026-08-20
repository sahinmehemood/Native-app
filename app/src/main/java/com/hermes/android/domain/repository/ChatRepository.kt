package com.hermes.android.domain.repository

import com.hermes.android.domain.model.ChatEvent
import kotlinx.coroutines.flow.Flow

/**
 * Streams chat completions. Falls back to the bundled offline mock when no backend
 * is configured or the backend is unreachable (offline-first).
 */
interface ChatRepository {
    fun stream(
        sessionId: String?,
        message: String,
        agentId: String? = null
    ): Flow<ChatEvent>
}
