package com.hermes.android.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Dark-terminal Material 3 theme. App defaults to Dark.
 * See docs/DESIGN_SYSTEM.md for palette.
 */

private val DarkColorScheme = darkColorScheme(
    primary = HermesColors.Primary,
    onPrimary = HermesColors.OnPrimary,
    primaryContainer = HermesColors.PrimaryContainer,
    onPrimaryContainer = HermesColors.OnPrimaryContainer,
    inversePrimary = HermesColors.PrimaryDim,
    secondary = HermesColors.Secondary,
    onSecondary = HermesColors.OnSecondary,
    tertiary = HermesColors.Tertiary,
    onTertiary = HermesColors.OnTertiary,
    error = HermesColors.Error,
    onError = HermesColors.OnError,
    errorContainer = HermesColors.ErrorContainer,
    onErrorContainer = Color(0xFFFCA5A5),
    background = HermesColors.Background,
    onBackground = HermesColors.OnSurface,
    surface = HermesColors.Surface,
    onSurface = HermesColors.OnSurface,
    surfaceVariant = HermesColors.SurfaceVariant,
    onSurfaceVariant = HermesColors.OnSurfaceVariant,
    outline = HermesColors.Outline,
    outlineVariant = HermesColors.OutlineVariant,
    surfaceContainerHighest = HermesColors.SurfaceContainerHighest,
    surfaceContainerHigh = HermesColors.SurfaceVariant,
    surfaceContainer = HermesColors.SurfaceVariant,
    surfaceContainerLow = HermesColors.Surface,
    inverseSurface = HermesColors.OnSurface,
    inverseOnSurface = HermesColors.Background,
    scrim = Color(0xCC000000),
)

private val AmoledColorScheme = darkColorScheme(
    primary = HermesColors.Primary,
    onPrimary = HermesColors.OnPrimary,
    primaryContainer = HermesColors.PrimaryContainer,
    onPrimaryContainer = HermesColors.OnPrimaryContainer,
    secondary = HermesColors.Secondary,
    onSecondary = HermesColors.OnSecondary,
    tertiary = HermesColors.Tertiary,
    onTertiary = HermesColors.OnTertiary,
    error = HermesColors.Error,
    onError = HermesColors.OnError,
    background = HermesColors.AmoledBackground,
    onBackground = HermesColors.OnSurface,
    surface = HermesColors.AmoledSurface,
    onSurface = HermesColors.OnSurface,
    surfaceVariant = HermesColors.SurfaceVariant,
    onSurfaceVariant = HermesColors.OnSurfaceVariant,
    outline = HermesColors.Outline,
    outlineVariant = HermesColors.OutlineVariant,
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF15803D),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCFCE7),
    onPrimaryContainer = Color(0xFF052E16),
    secondary = HermesColors.Secondary,
    onSecondary = Color(0xFFFFFFFF),
    tertiary = HermesColors.Tertiary,
    onTertiary = Color(0xFF0A0A0A),
    error = HermesColors.Error,
    onError = Color(0xFFFFFFFF),
    background = HermesColors.LightBackground,
    onBackground = HermesColors.LightOnSurface,
    surface = HermesColors.LightSurface,
    onSurface = HermesColors.LightOnSurface,
    surfaceVariant = HermesColors.LightSurfaceVariant,
    onSurfaceVariant = HermesColors.LightOnSurfaceVariant,
    outline = Color(0xFFDDDDDD),
    outlineVariant = Color(0xFFCCCCCC),
)

enum class HermesThemeMode { SYSTEM, DARK, LIGHT, AMOLED }

@Composable
fun HermesTheme(
    mode: HermesThemeMode = HermesThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val useDark = when (mode) {
        HermesThemeMode.SYSTEM -> isSystemInDarkTheme()
        HermesThemeMode.DARK -> true
        HermesThemeMode.AMOLED -> true
        HermesThemeMode.LIGHT -> false
    }
    val scheme = when (mode) {
        HermesThemeMode.AMOLED -> AmoledColorScheme
        HermesThemeMode.LIGHT -> LightColorScheme
        else -> if (useDark) DarkColorScheme else LightColorScheme
    }
    MaterialTheme(
        colorScheme = scheme,
        typography = HermesTypography,
        shapes = androidx.compose.material3.Shapes(
            small = HermesShapes.Small,
            medium = HermesShapes.Medium,
            large = HermesShapes.Large,
            extraLarge = HermesShapes.ExtraLarge
        ),
        content = content
    )
}
