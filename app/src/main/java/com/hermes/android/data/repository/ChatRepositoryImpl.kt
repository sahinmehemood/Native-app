package com.hermes.android.data.repository

import com.hermes.android.data.api.HermesApiService
import com.hermes.android.data.api.SseClient
import com.hermes.android.data.api.dto.ChatRequestDto
import com.hermes.android.data.source.local.MockLocalSource
import com.hermes.android.domain.model.ChatEvent
import com.hermes.android.domain.repository.ChatRepository
import com.hermes.android.domain.repository.ConnectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val api: HermesApiService,
    private val connectionRepository: ConnectionRepository,
    private val mock: MockLocalSource
) : ChatRepository {
    override fun stream(
        sessionId: String?,
        message: String,
        agentId: String?
    ): Flow<ChatEvent> = flow {
        val config = connectionRepository.observe().first()
        if (config == null) {
            emitAll(mock.streamReply(message))
            return@flow
        }
        try {
            val response = api.postChat(ChatRequestDto(message, sessionId, profile = agentId))
            emitAll(SseClient.parse(response))
        } catch (_: Exception) {
            // Offline-first: fall back to the bundled mock so the UI always responds.
            emitAll(mock.streamReply(message))
        }
    }
}
