package com.hermes.android.feature.activity.viewmodel

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
 * Builds an activity timeline from real data: the list of sessions (most recent
 * first) with each row expandable to reveal its recent messages. There is no
 * dedicated server "runs" endpoint, so the timeline is derived honestly from the
 * session list and per-session message history (docs/HERMES-MOBILE-API.md).
 *
 * Messages are fetched per-row on first expand (not eagerly across every
 * session) to keep the initial load cheap and respect the user's bandwidth.
 */
class ActivityViewModel(private val repository: SessionRepository) : ViewModel() {
    private val _state = MutableStateFlow(ActivityUiState())
    val state: StateFlow<ActivityUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.update { it.copy(status = ActivityStatus.Loading, connection = ConnectionStatus.Connected) }
        viewModelScope.launch {
            runCatching { repository.getSessions() }
                .fold(
                    onSuccess = { sessions ->
                        val items = sessions
                            .sortedByDescending { it.updatedAt ?: "" }
                            .map { ActivityItem(session = it) }
                        _state.update {
                            it.copy(
                                status = if (items.isEmpty()) ActivityStatus.Empty else ActivityStatus.Ready,
                                items = items,
                            )
                        }
                    },
                    onFailure = { e ->
                        if (e is IOException) {
                            _state.update { it.copy(status = ActivityStatus.Offline, connection = ConnectionStatus.Offline) }
                        } else {
                            _state.update { it.copy(status = ActivityStatus.Error, error = e.message ?: "Failed to load activity") }
                        }
                    },
                )
        }
    }

    fun toggleExpand(sessionId: String) {
        val items = _state.value.items
        val target = items.find { it.session.id == sessionId } ?: return
        val willExpand = !target.expanded
        _state.update {
            it.copy(items = items.map { item -> if (item.session.id == sessionId) item.copy(expanded = willExpand) else item })
        }
        if (willExpand && target.messagesStatus == ItemMessagesStatus.Idle) loadMessages(sessionId)
    }

    fun reloadMessages(sessionId: String) = loadMessages(sessionId)

    private fun loadMessages(sessionId: String) {
        _state.update {
            it.copy(
                items = it.items.map { item ->
                    if (item.session.id == sessionId) {
                        item.copy(messagesStatus = ItemMessagesStatus.Loading, messagesError = null)
                    } else {
                        item
                    }
                },
            )
        }
        viewModelScope.launch {
            runCatching { repository.getSessionMessages(sessionId) }
                .fold(
                    onSuccess = { messages ->
                        _state.update {
                            it.copy(
                                items = it.items.map { item ->
                                    if (item.session.id == sessionId) {
                                        item.copy(
                                            messages = messages,
                                            messagesStatus = if (messages.isEmpty()) ItemMessagesStatus.Empty else ItemMessagesStatus.Ready,
                                        )
                                    } else {
                                        item
                                    }
                                },
                            )
                        }
                    },
                    onFailure = { e ->
                        val status = if (e is IOException) ItemMessagesStatus.Offline else ItemMessagesStatus.Error
                        _state.update {
                            it.copy(
                                items = it.items.map { item ->
                                    if (item.session.id == sessionId) {
                                        item.copy(messagesStatus = status, messagesError = e.message ?: "Failed to load messages")
                                    } else {
                                        item
                                    }
                                },
                            )
                        }
                    },
                )
        }
    }

    fun onReconnect() = load()
}
