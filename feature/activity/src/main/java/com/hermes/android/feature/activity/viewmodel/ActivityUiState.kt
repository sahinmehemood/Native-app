package com.hermes.android.feature.activity.viewmodel

import com.hermes.android.core.gateway.ConnectionStatus
import com.hermes.android.core.gateway.model.Message
import com.hermes.android.core.gateway.model.SessionSummary

enum class ActivityStatus { Loading, Ready, Empty, Error, Offline }

/** Load state for the lazily-expanded message history of one timeline row. */
enum class ItemMessagesStatus { Idle, Loading, Ready, Empty, Error, Offline }

/**
 * One row in the activity timeline: a session plus its (lazily loaded) recent
 * messages and expansion state.
 */
data class ActivityItem(
    val session: SessionSummary,
    val expanded: Boolean = false,
    val messages: List<Message> = emptyList(),
    val messagesStatus: ItemMessagesStatus = ItemMessagesStatus.Idle,
    val messagesError: String? = null,
)

data class ActivityUiState(
    val status: ActivityStatus = ActivityStatus.Loading,
    val items: List<ActivityItem> = emptyList(),
    val connection: ConnectionStatus = ConnectionStatus.Connected,
    val error: String? = null,
)
