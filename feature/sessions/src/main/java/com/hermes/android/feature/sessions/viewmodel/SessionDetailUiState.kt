package com.hermes.android.feature.sessions.viewmodel

import com.hermes.android.core.gateway.ConnectionStatus
import com.hermes.android.core.gateway.model.Message

enum class SessionDetailStatus { Loading, Ready, Empty, Error, Offline }

data class SessionDetailUiState(
    val status: SessionDetailStatus = SessionDetailStatus.Loading,
    val sessionId: String = "",
    val title: String? = null,
    val messages: List<Message> = emptyList(),
    val connection: ConnectionStatus = ConnectionStatus.Connected,
    val error: String? = null,
)
