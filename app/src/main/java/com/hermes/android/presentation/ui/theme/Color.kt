package com.hermes.android.presentation.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Hermes dark-terminal palette. App defaults to dark.
 * Values per docs/DESIGN_SYSTEM.md.
 */
object HermesColors {
    // Backgrounds
    val Background = Color(0xFF0A0A0A)
    val Surface = Color(0xFF111111)
    val SurfaceVariant = Color(0xFF1A1A1A)
    val SurfaceContainerHighest = Color(0xFF1E1E1E)

    // Borders
    val Outline = Color(0xFF222222)
    val OutlineVariant = Color(0xFF333333)

    // Brand / primary
    val Primary = Color(0xFF22C55E)
    val OnPrimary = Color(0xFF0A0A0A)
    val PrimaryContainer = Color(0xFF166534)
    val OnPrimaryContainer = Color(0xFFDCFCE7)
    val PrimaryDim = Color(0xFF15803D)

    // Secondary / tertiary
    val Secondary = Color(0xFF3B82F6)
    val OnSecondary = Color(0xFF0A0A0A)
    val Tertiary = Color(0xFFF59E0B)
    val OnTertiary = Color(0xFF0A0A0A)

    // Status
    val Error = Color(0xFFEF4444)
    val OnError = Color(0xFFFFFFFF)
    val ErrorContainer = Color(0xFF450A0A)
    val Success = Color(0xFF22C55E)
    val Info = Color(0xFF3B82F6)
    val Warning = Color(0xFFF59E0B)

    // Text
    val OnSurface = Color(0xFFFAFAFA)
    val OnSurfaceVariant = Color(0xFF888888)
    val TextMuted = Color(0xFF555555)

    // Light theme (optional)
    val LightBackground = Color(0xFFFAFAFA)
    val LightSurface = Color(0xFFFFFFFF)
    val LightSurfaceVariant = Color(0xFFF1F1F1)
    val LightOnSurface = Color(0xFF0A0A0A)
    val LightOnSurfaceVariant = Color(0xFF555555)

    // AMOLED
    val AmoledBackground = Color(0xFF000000)
    val AmoledSurface = Color(0xFF050505)
}
