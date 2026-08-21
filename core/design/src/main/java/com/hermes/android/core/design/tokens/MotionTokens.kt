package com.hermes.android.core.design.tokens

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.ui.unit.Duration
import androidx.compose.ui.unit.IntMillis

/**
 * Motion spec — the app's movement language. Every animation references these
 * durations/easings so motion feels consistent and respects [ReducedMotion].
 *
 * Figma prototypes export matching curves; the design-system agent maps
 * Figma's "ease-in-out" / "spring" to these exactly.
 */
object HermesMotion {
    // Durations (ms)
    val DurationInstant = 0
    val DurationFast = 150
    val DurationBase = 250
    val DurationSlow = 350
    val DurationEmphasis = 500

    // Easings
    val EaseStandard: Easing = FastOutSlowInEasing
    val EaseDecelerate: Easing = LinearOutSlowInEasing
    val EaseLinear: Easing = LinearEasing

    /** Compose [Duration] helper. */
    fun duration(ms: Int): Duration = Duration(IntMillis(ms))
}

/**
 * User motion preference. When [enabled] is false, every animated value in the
 * app snaps instantly (no enter/exit, no positional animation). Driven by
 * Settings → Appearance and the system "reduce motion" accessibility flag.
 */
data class ReducedMotion(val enabled: Boolean = false)
