package com.hermes.android.feature.home.viewmodel

import com.hermes.android.core.gateway.ConnectionStatus
import com.hermes.android.core.gateway.model.HealthStatus
import com.hermes.android.core.gateway.model.SessionSummary

sealed interface HealthLoad {
    data object Loading : HealthLoad
    data class Ready(val status: HealthStatus) : HealthLoad
    data class Error(val message: String) : HealthLoad
    data object Offline : HealthLoad
}

sealed interface SessionsLoad {
    data object Loading : SessionsLoad
    data class Ready(val sessions: List<SessionSummary>) : SessionsLoad
    data object Empty : SessionsLoad
    data class Error(val message: String) : SessionsLoad
    data object Offline : SessionsLoad
}

data class HomeUiState(
    val health: HealthLoad = HealthLoad.Loading,
    val sessions: SessionsLoad = SessionsLoad.Loading,
    val connection: ConnectionStatus = ConnectionStatus.Connected,
)
