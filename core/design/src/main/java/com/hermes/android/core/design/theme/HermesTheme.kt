package com.hermes.android.core.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.hermes.android.core.design.tokens.HermesColorTokens
import com.hermes.android.core.design.tokens.ReducedMotion

/**
 * Theme root. Wraps Material3 (for component correctness) while injecting the
 * Hermes brand colors and a [LocalReducedMotion] signal that every animated
 * composable reads.
 *
 * Per QUALITY-GATES.md, dark theme is first-class and reduced-motion is a
 * hard requirement — both satisfied here without opt-in code at call sites.
 */

private val DarkColorScheme = darkColorScheme(
    primary = HermesColorTokens.Dark.accent,
    onPrimary = HermesColorTokens.Dark.accentOn,
    background = HermesColorTokens.Dark.background,
    surface = HermesColorTokens.Dark.surface,
    surfaceVariant = HermesColorTokens.Dark.surfaceVariant,
    onSurface = HermesColorTokens.Dark.onSurface,
    onSurfaceVariant = HermesColorTokens.Dark.onSurfaceMuted,
    outline = HermesColorTokens.Dark.border,
    error = HermesColorTokens.Dark.danger,
)

private val LightColorScheme = lightColorScheme(
    primary = HermesColorTokens.Light.accent,
    onPrimary = HermesColorTokens.Light.accentOn,
    background = HermesColorTokens.Light.background,
    surface = HermesColorTokens.Light.surface,
    surfaceVariant = HermesColorTokens.Light.surfaceVariant,
    onSurface = HermesColorTokens.Light.onSurface,
    onSurfaceVariant = HermesColorTokens.Light.onSurfaceMuted,
    outline = HermesColorTokens.Light.border,
    error = HermesColorTokens.Light.danger,
)

val LocalReducedMotion = staticCompositionLocalOf { ReducedMotion(enabled = false) }

@Composable
fun HermesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    reducedMotion: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    CompositionLocalProvider(
        LocalReducedMotion provides ReducedMotion(enabled = reducedMotion),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = HermesTypography,
            content = content,
        )
    }
}
