package com.hermes.android.feature.chat.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hermes.android.core.design.tokens.HermesSpacing

@Composable
fun Composer(
    draft: String,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        OutlinedTextField(
            value = draft,
            onValueChange = onDraftChange,
            modifier = Modifier.weight(1f).defaultMinSize(minHeight = HermesSpacing.TouchTarget),
            placeholder = { Text("Message Hermes…") },
            maxLines = 6,
            textStyle = MaterialTheme.typography.bodyLarge,
        )
        Spacer(Modifier.width(HermesSpacing.Sm))
        IconButton(
            onClick = onSend,
            enabled = enabled && draft.isNotBlank(),
            modifier = Modifier.width(HermesSpacing.TouchTarget).defaultMinSize(minHeight = HermesSpacing.TouchTarget),
        ) {
            Icon(Icons.Filled.Send, contentDescription = "Send message")
        }
    }
}
