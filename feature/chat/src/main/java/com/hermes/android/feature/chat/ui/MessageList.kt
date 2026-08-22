package com.hermes.android.feature.chat.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hermes.android.core.design.theme.LocalReducedMotion
import com.hermes.android.core.design.tokens.HermesColorTokens
import com.hermes.android.core.design.tokens.HermesElevation
import com.hermes.android.core.design.tokens.HermesSpacing
import com.hermes.android.feature.chat.viewmodel.ChatMessage
import com.hermes.android.feature.chat.viewmodel.ToolActivity

@Composable
fun MessageList(
    messages: List<ChatMessage>,
    toolActivity: List<ToolActivity>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        if (toolActivity.isNotEmpty()) {
            item { ToolChipsRow(tools = toolActivity) }
        }
        items(messages, key = { it.id }) { message -> MessageBubble(message = message) }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val isDark = isSystemInDarkTheme()
    val accentSoft = if (isDark) HermesColorTokens.Dark.accentSoft else HermesColorTokens.Light.accentSoft
    val accentOn = if (isDark) HermesColorTokens.Dark.accentOn else HermesColorTokens.Light.accentOn
    val bubbleColor = if (message.role == "user") accentSoft else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (message.role == "user") accentOn else MaterialTheme.colorScheme.onSurface
    val alignment = if (message.role == "user") Alignment.CenterEnd else Alignment.CenterStart
    Box(Modifier.fillMaxWidth().padding(vertical = HermesSpacing.Xs), contentAlignment = alignment) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(HermesSpacing.CardRadius),
            tonalElevation = HermesElevation.Low,
        ) {
            Column(Modifier.padding(HermesSpacing.Md)) {
                Text(
                    text = message.content.ifBlank { "…" },
                    color = contentColor,
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (message.isStreaming) StreamingIndicator()
            }
        }
    }
}

@Composable
fun StreamingIndicator() {
    val reduced = LocalReducedMotion.current
    if (reduced.enabled) {
        Text(
            "Hermes is typing…",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    } else {
        CircularProgressIndicator(modifier = Modifier.fillMaxWidth(0.12f), strokeWidth = HermesSpacing.Xs)
    }
}
