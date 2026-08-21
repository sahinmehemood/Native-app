package com.hermes.android.feature.chat.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hermes.android.core.design.theme.HermesTheme
import com.hermes.android.core.gateway.ConnectionStatus
import com.hermes.android.feature.chat.viewmodel.ChatMessage
import com.hermes.android.feature.chat.viewmodel.ChatUiState
import com.hermes.android.feature.chat.viewmodel.PendingApproval
import com.hermes.android.feature.chat.viewmodel.ToolActivity

@Preview(name = "Chat (Dark)")
@Composable
fun ChatScreenPreviewDark() {
    HermesTheme(darkTheme = true) {
        ChatScreen(
            state = sampleChatState(),
            onDraftChange = {},
            onSend = {},
            onApproval = { _, _ -> },
            onStop = {},
            onReconnect = {},
        )
    }
}

@Preview(name = "Chat (Light)", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ChatScreenPreviewLight() {
    HermesTheme(darkTheme = false) {
        ChatScreen(
            state = sampleChatState(),
            onDraftChange = {},
            onSend = {},
            onApproval = { _, _ -> },
            onStop = {},
            onReconnect = {},
        )
    }
}

private fun sampleChatState(): ChatUiState = ChatUiState(
    connection = ConnectionStatus.Connected,
    messages = listOf(
        ChatMessage(id = "u1", role = "user", content = "Summarize the logs"),
        ChatMessage(id = "a1", role = "assistant", content = "On it — streaming the summary now.", isStreaming = true),
    ),
    toolActivity = listOf(
        ToolActivity(index = 0, toolName = "read_logs", phase = "start", ok = true, duration = 0.0, preview = null),
    ),
    isStreaming = true,
    pendingApproval = PendingApproval(
        runId = "r1",
        title = "Run shell command",
        detail = "rm -rf /tmp/cache",
        choices = listOf("once", "session", "deny"),
    ),
)
