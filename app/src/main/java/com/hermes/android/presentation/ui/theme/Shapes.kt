package com.hermes.android.presentation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

/**
 * Corner radii per docs/DESIGN_SYSTEM.md.
 */
object HermesShapes {
    val Small = RoundedCornerShape(8.dp)
    val Medium = RoundedCornerShape(12.dp)
    val Large = RoundedCornerShape(16.dp)
    val ExtraLarge = RoundedCornerShape(28.dp)
    val Full = RoundedCornerShape(percent = 50)
}
