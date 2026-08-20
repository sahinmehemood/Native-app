package com.hermes.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.domain.model.ChatEvent
import com.hermes.android.domain.model.Message
import com.hermes.android.domain.model.MessageRole
import com.hermes.android.domain.model.MessageStatus
import com.hermes.android.domain.usecase.ObserveAgentsUseCase
import com.hermes.android.domain.usecase.SendMessageUseCase
import com.hermes.android.presentation.state.ChatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessage: SendMessageUseCase,
    observeAgents: ObserveAgentsUseCase
) : ViewModel() {
    private val sessionId = "default"
    private var activeAgentId: String? = null

    private val _state = MutableStateFlow<ChatUiState>(ChatUiState.Ready(emptyList(), false, "", null, 0, 0))
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeAgents().collect { agents ->
                val active = agents.firstOrNull { it.isActive } ?: agents.firstOrNull()
                activeAgentId = active?.id
                (_state.value as? ChatUiState.Ready)?.let { s ->
                    _state.value = s.copy(agentName = active?.name)
                }
            }
        }
    }

    fun onInputChange(text: String) {
        (_state.value as? ChatUiState.Ready)?.let { s -> _state.value = s.copy(input = text) }
    }

    fun send() {
        val cur = _state.value as? ChatUiState.Ready ?: return
        val text = cur.input.trim()
        if (text.isBlank() || cur.streaming) return

        val userMsg = Message(UUID.randomUUID().toString(), sessionId, MessageRole.USER, text)
        val assistantMsg = Message(
            UUID.randomUUID().toString(), sessionId, MessageRole.ASSISTANT, "",
            status = MessageStatus.STREAMING
        )
        _state.value = cur.copy(
            messages = cur.messages + userMsg + assistantMsg,
            input = "", streaming = true, tokensIn = 0, tokensOut = 0
        )

        viewModelScope.launch {
            var acc = ""
            var tin = 0
            var tout = 0
            try {
                sendMessage(sessionId, text, activeAgentId).collect { event ->
                    when (event) {
                        is ChatEvent.MessageDelta -> {
                            acc += event.delta
                            patchAssistant(assistantMsg.id) { it.copy(content = acc) }
                        }
                        is ChatEvent.MessageEnd -> {
                            tin = event.tokensIn
                            tout = event.tokensOut
                        }
                        else -> Unit
                    }
                }
                patchAssistant(assistantMsg.id) {
                    it.copy(status = MessageStatus.DONE, tokensIn = tin, tokensOut = tout)
                }
                (_state.value as? ChatUiState.Ready)?.let { s ->
                    _state.value = s.copy(streaming = false, tokensIn = tin, tokensOut = tout)
                }
            } catch (e: Exception) {
                patchAssistant(assistantMsg.id) {
                    it.copy(status = MessageStatus.ERROR, content = acc.ifBlank { "⚠ ${e.message}" })
                }
                (_state.value as? ChatUiState.Ready)?.let { s -> _state.value = s.copy(streaming = false) }
            }
        }
    }

    private fun patchAssistant(id: String, transform: (Message) -> Message) {
        (_state.value as? ChatUiState.Ready)?.let { s ->
            _state.value = s.copy(messages = s.messages.map { if (it.id == id) transform(it) else it })
        }
    }
}
