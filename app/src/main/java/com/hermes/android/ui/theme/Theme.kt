package com.hermes.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HermesScheme = darkColorScheme(
    background = Color(0xFF0B0D0F),
    surface = Color(0xFF15191D),
    primary = Color(0xFFD9A441),
    onPrimary = Color(0xFF0B0D0F),
    onBackground = Color(0xFFF2F3F4),
    onSurface = Color(0xFFF2F3F4),
)

@Composable
fun HermesTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = HermesScheme, content = content)
}

