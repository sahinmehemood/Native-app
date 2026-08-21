package com.hermes.android.core.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
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
    primary = HermesColorTokens.Accent,
    onPrimary = HermesColorTokens.AccentOn,
    background = HermesColorTokens.Dark.Background,
    surface = HermesColorTokens.Dark.Surface,
    surfaceVariant = HermesColorTokens.Dark.SurfaceVariant,
    onSurface = HermesColorTokens.Dark.OnSurface,
    onSurfaceVariant = HermesColorTokens.Dark.OnSurfaceMuted,
    outline = HermesColorTokens.Dark.Border,
    error = HermesColorTokens.Danger,
)

private val LightColorScheme = lightColorScheme(
    primary = HermesColorTokens.Accent,
    onPrimary = HermesColorTokens.AccentOn,
    background = HermesColorTokens.Light.Background,
    surface = HermesColorTokens.Light.Surface,
    surfaceVariant = HermesColorTokens.Light.SurfaceVariant,
    onSurface = HermesColorTokens.Light.OnSurface,
    onSurfaceVariant = HermesColorTokens.Light.OnSurfaceMuted,
    outline = HermesColorTokens.Light.Border,
    error = HermesColorTokens.Danger,
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
