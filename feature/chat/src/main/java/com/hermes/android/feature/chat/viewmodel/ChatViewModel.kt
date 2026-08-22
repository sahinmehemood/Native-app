package com.hermes.android.feature.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.core.gateway.ConnectionStatus
import com.hermes.android.core.gateway.HermesGatewayClient
import com.hermes.android.core.gateway.HermesJson
import com.hermes.android.core.gateway.PendingApprovalsStore
import com.hermes.android.core.gateway.model.ChatRequest
import com.hermes.android.core.gateway.model.RunStatusFrame
import com.hermes.android.core.gateway.model.StreamEvent
import com.hermes.android.feature.chat.domain.ChatStreamReducer
import com.hermes.android.feature.chat.domain.DraftRepository
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import java.io.IOException
import java.util.UUID

class ChatViewModel(
    private val gateway: HermesGatewayClient,
    private val pendingApprovals: PendingApprovalsStore,
    private val draftRepository: DraftRepository,
    private val reducer: ChatStreamReducer = ChatStreamReducer(),
    private val sessionId: String = "session-unknown",
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState(draft = draftRepository.get(sessionId)))
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    private var activeRequestId: String? = null
    private var activeRunId: String? = null

    fun onDraftChange(text: String) {
        _state.update { it.copy(draft = text) }
        draftRepository.save(sessionId, text)
    }

    fun send() {
        val draft = _state.value.draft
        if (draft.isBlank() || activeRequestId != null) return
        activeRequestId = UUID.randomUUID().toString()
        val userMessage = ChatMessage(id = "u-${System.nanoTime()}", role = "user", content = draft)
        _state.update {
            it.copy(
                messages = it.messages + userMessage,
                isStreaming = true,
                error = null,
                draft = "",
            )
        }
        draftRepository.save(sessionId, "")
        viewModelScope.launch {
            try {
                gateway.postChat(sessionId, ChatRequest(message = draft)).collect { event -> handleEvent(event) }
                activeRequestId = null
                _state.update { it.copy(isStreaming = false) }
            } catch (e: IOException) {
                activeRequestId = null
                _state.update {
                    it.copy(
                        isStreaming = false,
                        connection = ConnectionStatus.Offline,
                        error = e.message ?: "Connection lost",
                    )
                }
            } catch (e: Exception) {
                activeRequestId = null
                _state.update { it.copy(isStreaming = false, error = e.message ?: "Something went wrong") }
            }
        }
    }

    private fun handleEvent(event: StreamEvent) {
        if (event.event == "run") captureRun(event)
        _state.value = reducer.reduce(_state.value, event)
    }

    private fun captureRun(event: StreamEvent) {
        val run = runCatching { HermesJson.decodeFromJsonElement<RunStatusFrame>(event.data) }
            .getOrNull() ?: return
        activeRunId = run.runId
        if (run.status == "awaiting_approval") pendingApprovals.add(run.runId) else pendingApprovals.remove(run.runId)
    }

    fun onConnectionLost() {
        _state.update { it.copy(connection = ConnectionStatus.Reconnecting) }
    }

    fun onReconnect() {
        // HARD RULE (contract §5/§6): a reconnect MUST NOT auto-resend the last
        // prompt or auto-approve anything. We only restore the connection flag;
        // an in-flight run keeps its approval card for the user to resolve, and a
        // queued prompt is NOT resent.
        _state.update { it.copy(connection = ConnectionStatus.Connected) }
    }

    fun onApproval(decision: String, scope: String? = null) {
        val runId = activeRunId ?: _state.value.pendingApproval?.runId ?: return
        viewModelScope.launch {
            runCatching { gateway.postApproval(runId, decision, scope) }
            pendingApprovals.remove(runId)
            _state.update { it.copy(pendingApproval = null) }
        }
    }

    fun onStop() {
        val runId = activeRunId ?: return
        viewModelScope.launch { runCatching { gateway.stopRun(runId) } }
    }

    /** Cancel in-flight coroutines (used by tests and on permanent teardown). */
    fun dispose() {
        viewModelScope.cancel()
    }
}
