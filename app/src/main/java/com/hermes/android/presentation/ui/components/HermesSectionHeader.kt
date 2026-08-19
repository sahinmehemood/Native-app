package com.hermes.android.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hermes.android.presentation.ui.theme.HermesColors
import com.hermes.android.presentation.ui.theme.HermesSpacing

/**
 * Section header in the commandcode.ai style: "//01" index + mono title + muted subtitle.
 */
@Composable
fun HermesSectionHeader(
    index: String? = null,
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = HermesSpacing.Spacing8)) {
        if (index != null) {
            Text(
                text = index,
                style = MaterialTheme.typography.labelMedium,
                color = HermesColors.TextMuted
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = HermesColors.OnSurfaceVariant
            )
        }
    }
}
