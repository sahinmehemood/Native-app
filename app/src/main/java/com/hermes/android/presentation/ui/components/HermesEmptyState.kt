package com.hermes.android.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hermes.android.presentation.ui.theme.HermesColors
import com.hermes.android.presentation.ui.theme.HermesSpacing

@Composable
fun HermesEmptyState(
    title: String,
    message: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(HermesSpacing.Spacing32),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        if (!message.isNullOrBlank()) {
            Spacer(Modifier.height(HermesSpacing.Spacing8))
            Text(message, style = MaterialTheme.typography.bodyMedium, color = HermesColors.OnSurfaceVariant)
        }
        if (!actionText.isNullOrBlank() && onAction != null) {
            Spacer(Modifier.height(HermesSpacing.Spacing16))
            HermesButton(actionText, onAction)
        }
    }
}
