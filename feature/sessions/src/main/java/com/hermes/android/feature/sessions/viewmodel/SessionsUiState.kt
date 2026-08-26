package com.hermes.android.feature.sessions.viewmodel

import com.hermes.android.core.gateway.ConnectionStatus
import com.hermes.android.core.gateway.model.SessionSummary

/** Load lifecycle for the session list. */
enum class SessionsStatus { Loading, Ready, Empty, Error, Offline }

data class SessionsUiState(
    val status: SessionsStatus = SessionsStatus.Loading,
    val sessions: List<SessionSummary> = emptyList(),
    val query: String = "",
    val connection: ConnectionStatus = ConnectionStatus.Connected,
    val error: String? = null,
)

/** Derived, query-filtered view of the loaded sessions. */
fun SessionsUiState.filtered(): List<SessionSummary> {
    if (query.isBlank()) return sessions
    return sessions.filter { it.title?.contains(query, ignoreCase = true) == true }
}
