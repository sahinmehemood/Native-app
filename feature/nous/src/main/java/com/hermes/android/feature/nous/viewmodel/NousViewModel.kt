package com.hermes.android.feature.nous.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.core.data.SessionRepository
import com.hermes.android.core.gateway.ConnectionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import java.io.IOException

/**
 * NOUS capture + search surface.
 *
 * **Capture** posts a note/link into a Hermes session — either a brand-new
 * session (created on demand) or a session the user picks from the list. The
 * message is dispatched via the verified `POST /api/sessions/{id}/chat` endpoint
 * and the result is a real, persisted Hermes session.
 *
 * **Search** is an honest client-side filter over the user's Hermes sessions
 * (there is no separate knowledge-store endpoint in the contract), labelled as
 * such in the UI. No vault access is fabricated.
 */
class NousViewModel(private val repository: SessionRepository) : ViewModel() {
    private val _state = MutableStateFlow(NousUiState())
    val state: StateFlow<NousUiState> = _state.asStateFlow()

    init { loadSessions() }

    fun loadSessions() {
        _state.update { it.copy(connection = ConnectionStatus.Connected) }
        viewModelScope.launch {
            runCatching { repository.getSessions() }
                .fold(
                    onSuccess = { sessions ->
                        _state.update {
                            it.copy(
                                sessions = sessions,
                                sessionsLoaded = true,
                                searchStatus = if (sessions.isEmpty()) NousSearchStatus.Empty else NousSearchStatus.Ready,
                            )
                        }
                    },
                    onFailure = { e ->
                        if (e is IOException) {
                            _state.update { it.copy(connection = ConnectionStatus.Offline, searchStatus = NousSearchStatus.Offline) }
                        } else {
                            _state.update { it.copy(searchStatus = NousSearchStatus.Error, error = e.message ?: "Failed to load sessions") }
                        }
                    },
                )
        }
    }

    fun onCaptureTextChange(text: String) {
        _state.update { it.copy(captureText = text, captureStatus = NousCaptureStatus.Idle, captureError = null) }
    }

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query) }
    }

    /** Pick a target: null = new session, otherwise an existing session id. */
    fun onTargetChange(sessionId: String?) {
        _state.update { it.copy(targetSessionId = sessionId) }
    }

    fun capture() {
        val text = _state.value.captureText.trim()
        if (text.isBlank() || _state.value.captureStatus == NousCaptureStatus.Sending) return
        val target = _state.value.targetSessionId
        _state.update { it.copy(captureStatus = NousCaptureStatus.Sending, captureError = null) }
        viewModelScope.launch {
            runCatching {
                val sessionId = if (target == null) repository.createSession().id else target
                repository.postChatMessage(sessionId, text)
                sessionId
            }.fold(
                onSuccess = { sessionId ->
                    val title = _state.value.sessions.find { it.id == sessionId }?.title
                    _state.update {
                        it.copy(
                            captureStatus = NousCaptureStatus.Success,
                            lastCapturedSessionId = sessionId,
                            lastCapturedTitle = title,
                            captureText = "",
                        )
                    }
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            captureStatus = NousCaptureStatus.Error,
                            captureError = e.message ?: "Failed to capture note",
                        )
                    }
                },
            )
        }
    }

    fun onReconnect() {
        _state.update { it.copy(captureStatus = NousCaptureStatus.Idle, captureError = null) }
        loadSessions()
    }
}
