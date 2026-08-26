package com.hermes.android.feature.sessions.viewmodel

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
 * Renders a single session's message history (GET /api/sessions/{id}/messages).
 * Real data only — no synthetic content.
 */
class SessionDetailViewModel(
    private val sessionId: String,
    private val repository: SessionRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(SessionDetailUiState(sessionId = sessionId))
    val state: StateFlow<SessionDetailUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.update { it.copy(status = SessionDetailStatus.Loading, connection = ConnectionStatus.Connected) }
        viewModelScope.launch {
            runCatching { repository.getSessionMessages(sessionId) }
                .fold(
                    onSuccess = { messages ->
                        _state.update {
                            it.copy(
                                status = if (messages.isEmpty()) SessionDetailStatus.Empty else SessionDetailStatus.Ready,
                                messages = messages,
                            )
                        }
                    },
                    onFailure = { e ->
                        if (e is IOException) {
                            _state.update { it.copy(status = SessionDetailStatus.Offline, connection = ConnectionStatus.Offline) }
                        } else {
                            _state.update { it.copy(status = SessionDetailStatus.Error, error = e.message ?: "Failed to load messages") }
                        }
                    },
                )
        }
    }

    fun onReconnect() = load()
}
