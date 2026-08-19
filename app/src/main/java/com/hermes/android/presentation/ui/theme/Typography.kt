package com.hermes.android.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography per docs/DESIGN_SYSTEM.md.
 * Headings/Stats/Labels use a monospace family (built-in Monospace now;
 * JetBrains Mono .ttf will be bundled later in Phase 1 polish).
 * Body/Buttons/Inputs use the system font.
 */
private val Mono = FontFamily.Monospace
private val Sans = FontFamily.Default

val HermesTypography = Typography(
    displayLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 57.sp, lineHeight = 64.sp
    ),
    displayMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 45.sp, lineHeight = 52.sp
    ),
    headlineLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 32.sp, lineHeight = 40.sp
    ),
    headlineMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 28.sp, lineHeight = 36.sp
    ),
    titleLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 22.sp, lineHeight = 28.sp
    ),
    titleMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 24.sp
    ),
    bodyLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp
    ),
    bodyMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = Sans, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp
    ),
    labelLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp
    ),
    labelMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp
    ),
    labelSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp
    ),
)
