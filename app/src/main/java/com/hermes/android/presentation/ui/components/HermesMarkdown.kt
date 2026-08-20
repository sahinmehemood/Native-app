package com.hermes.android.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.hermes.android.presentation.ui.theme.HermesColors
import com.hermes.android.presentation.ui.theme.HermesShapes
import com.hermes.android.presentation.ui.theme.HermesSpacing

/**
 * Minimal, dependency-free markdown: renders fenced ```code``` blocks in a monospace
 * surface and everything else as body text. Full markdown support is backlog.
 */
@Composable
fun HermesMarkdown(content: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    val parts = content.split("```")
    Column(Modifier.fillMaxWidth()) {
        parts.forEachIndexed { i, part ->
            if (i % 2 == 1) {
                Surface(
                    color = HermesColors.SurfaceContainerHighest,
                    shape = HermesShapes.Small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        part.trim(),
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodyMedium,
                        color = HermesColors.OnSurface,
                        modifier = Modifier.padding(HermesSpacing.Spacing12)
                    )
                }
            } else if (part.isNotBlank()) {
                Text(part.trim(), style = MaterialTheme.typography.bodyLarge, color = color)
            }
            if (i < parts.lastIndex) Spacer(Modifier.height(HermesSpacing.Spacing8))
        }
    }
}
