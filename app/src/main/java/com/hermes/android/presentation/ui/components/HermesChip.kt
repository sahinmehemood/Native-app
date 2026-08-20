package com.hermes.android.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hermes.android.presentation.ui.theme.HermesColors
import com.hermes.android.presentation.ui.theme.HermesShapes
import com.hermes.android.presentation.ui.theme.HermesSpacing

@Composable
fun HermesChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container = if (selected) HermesColors.Primary else HermesColors.SurfaceVariant
    val contentColor = if (selected) HermesColors.OnPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        shape = HermesShapes.Medium,
        color = container,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
            modifier = Modifier.padding(horizontal = HermesSpacing.Spacing16, vertical = HermesSpacing.Spacing8)
        )
    }
}
