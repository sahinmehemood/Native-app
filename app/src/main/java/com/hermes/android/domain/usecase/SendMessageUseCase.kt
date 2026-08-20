package com.hermes.android.domain.usecase

import com.hermes.android.domain.model.ChatEvent
import com.hermes.android.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(
        sessionId: String?,
        message: String,
        agentId: String?
    ): Flow<ChatEvent> = chatRepository.stream(sessionId, message, agentId)
}
