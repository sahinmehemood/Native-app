package com.hermes.android.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hermes.android.presentation.ui.theme.HermesSpacing

@Composable
fun HermesChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(HermesSpacing.Spacing12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HermesTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = "Message Hermes…",
            modifier = Modifier.weight(1f),
            singleLine = false
        )
        Spacer(Modifier.width(HermesSpacing.Spacing8))
        HermesIconButton(
            icon = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send message",
            onClick = onSend,
            enabled = enabled && value.isNotBlank()
        )
    }
}
