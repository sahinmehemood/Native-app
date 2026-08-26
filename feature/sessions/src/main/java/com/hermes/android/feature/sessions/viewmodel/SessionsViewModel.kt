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
 * Browses Hermes sessions: lists them, supports title search and pull-to-refresh,
 * and deletes a session.
 *
 * All load failures are classified into [SessionsStatus.Error] (server/parse) or
 * [SessionsStatus.Offline] (no route to gateway) so the UI can render the right
 * surface per AGENTS.md (loading / empty / error / offline).
 */
class SessionsViewModel(private val repository: SessionRepository) : ViewModel() {
    private val _state = MutableStateFlow(SessionsUiState())
    val state: StateFlow<SessionsUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.update { it.copy(status = SessionsStatus.Loading, connection = ConnectionStatus.Connected) }
        viewModelScope.launch {
            runCatching { repository.getSessions() }
                .fold(
                    onSuccess = { sessions ->
                        _state.update {
                            it.copy(
                                status = if (sessions.isEmpty()) SessionsStatus.Empty else SessionsStatus.Ready,
                                sessions = sessions,
                            )
                        }
                    },
                    onFailure = { e ->
                        if (e is IOException) {
                            _state.update { it.copy(status = SessionsStatus.Offline, connection = ConnectionStatus.Offline) }
                        } else {
                            _state.update { it.copy(status = SessionsStatus.Error, error = e.message ?: "Failed to load sessions") }
                        }
                    },
                )
        }
    }

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query) }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            runCatching { repository.deleteSession(sessionId) }
                .onSuccess { _state.update { it.copy(sessions = it.sessions.filter { s -> s.id != sessionId }) } }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            status = if (e is IOException) SessionsStatus.Offline else SessionsStatus.Error,
                            error = e.message ?: "Failed to delete session",
                        )
                    }
                }
        }
    }

    fun onReconnect() = load()
}
