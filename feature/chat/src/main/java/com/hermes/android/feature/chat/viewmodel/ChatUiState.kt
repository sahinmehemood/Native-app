package com.hermes.android.feature.chat.viewmodel

import com.hermes.android.core.gateway.ConnectionStatus

data class ChatMessage(
    val id: String,
    val role: String, // user | assistant | system | tool
    val content: String,
    val isStreaming: Boolean = false,
)

data class ToolActivity(
    val index: Int,
    val toolName: String,
    val phase: String, // start | finish
    val ok: Boolean,
    val duration: Double,
    val preview: String?,
)

data class PendingApproval(
    val runId: String,
    val title: String?,
    val detail: String?,
    val choices: List<String>,
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val toolActivity: List<ToolActivity> = emptyList(),
    val connection: ConnectionStatus = ConnectionStatus.Connected,
    val draft: String = "",
    val pendingApproval: PendingApproval? = null,
    val isStreaming: Boolean = false,
    val error: String? = null,
)
