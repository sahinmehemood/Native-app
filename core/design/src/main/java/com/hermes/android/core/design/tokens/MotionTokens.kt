package com.hermes.android.core.design.tokens

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing

object HermesMotion {
    val DurationInstant = 0
    val DurationFast = 150
    val DurationBase = 250
    val DurationSlow = 350
    val DurationEmphasis = 500
    val EaseStandard: Easing = FastOutSlowInEasing
    val EaseDecelerate: Easing = LinearOutSlowInEasing
    val EaseLinear: Easing = LinearEasing
}

data class ReducedMotion(val enabled: Boolean = false)
