package com.hermes.android.feature.nous.viewmodel

import com.hermes.android.core.gateway.ConnectionStatus
import com.hermes.android.core.gateway.model.SessionSummary

enum class NousCaptureStatus { Idle, Sending, Success, Error }
enum class NousSearchStatus { Idle, Loading, Ready, Empty, Error, Offline }

data class NousUiState(
    val sessions: List<SessionSummary> = emptyList(),
    val sessionsLoaded: Boolean = false,
    val captureText: String = "",
    /** null = capture into a new session; otherwise the chosen session id. */
    val targetSessionId: String? = null,
    val captureStatus: NousCaptureStatus = NousCaptureStatus.Idle,
    val captureError: String? = null,
    val lastCapturedSessionId: String? = null,
    val lastCapturedTitle: String? = null,
    val query: String = "",
    val searchStatus: NousSearchStatus = NousSearchStatus.Idle,
    val connection: ConnectionStatus = ConnectionStatus.Connected,
    val error: String? = null,
)

/** Client-side filtered sessions for the search surface. */
fun NousUiState.filteredSessions(): List<SessionSummary> {
    if (query.isBlank()) return sessions
    return sessions.filter {
        it.title?.contains(query, ignoreCase = true) == true ||
            it.id.contains(query, ignoreCase = true)
    }
}
