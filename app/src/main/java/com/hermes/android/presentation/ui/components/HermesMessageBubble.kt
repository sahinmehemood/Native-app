package com.hermes.android.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hermes.android.domain.model.Message
import com.hermes.android.domain.model.MessageRole
import com.hermes.android.domain.model.MessageStatus
import com.hermes.android.presentation.ui.theme.HermesColors
import com.hermes.android.presentation.ui.theme.HermesShapes
import com.hermes.android.presentation.ui.theme.HermesSpacing

@Composable
fun HermesMessageBubble(message: Message) {
    val isUser = message.role == MessageRole.USER
    val textColor = if (isUser) HermesColors.OnPrimary else MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = HermesShapes.Medium,
            color = if (isUser) HermesColors.Primary else HermesColors.SurfaceVariant,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(Modifier.padding(HermesSpacing.Spacing12)) {
                if (message.content.isBlank() && message.status == MessageStatus.STREAMING) {
                    Row(verticalAlignment = Alignment.CenterVertically) { OrbLoader(size = 18.dp) }
                } else {
                    HermesMarkdown(message.content, color = textColor)
                }
            }
        }
    }
}
