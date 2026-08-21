package com.hermes.android.core.design.tokens

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp

/**
 * Spacing scale — 4dp base grid, named (not magic numbers) so every screen
 * shares rhythm. Composables reference [HermesSpacing] never a raw `dp` literal.
 */
object HermesSpacing {
    val None = 0.dp
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 12.dp
    val Lg = 16.dp
    val Xl = 24.dp
    val Xxl = 32.dp
    val Section = 40.dp

    // Component-specific
    val TouchTarget = 48.dp   // minimum tappable area (a11y)
    val CardRadius = 16.dp
    val SheetRadius = 28.dp
    val ChipRadius = 999.dp   // pill
    val ContentMaxWidth = 720.dp // central column cap on tablets/foldables
}

/**
 * Elevation / depth — restrained, Hermes uses flat surfaces with hairline
 * borders rather than heavy shadows.
 */
object HermesElevation {
    val None = 0.dp
    val Low = 1.dp
    val Medium = 3.dp
    val High = 6.dp
    val Drag = 12.dp
}

/**
 * Typography roles mapped onto Material3's typography (set in [HermesTheme]).
 * Names reflect intent, not size, so designers can retune sizes in Figma
 * without touching call sites.
 */
enum class HermesTextStyle {
    Display,
    TitleLarge,
    Title,
    BodyLarge,
    Body,
    Label,
    Caption,
    Code,
}
