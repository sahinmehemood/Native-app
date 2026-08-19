package com.hermes.android.presentation.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hermes.android.presentation.ui.theme.HermesColors

/**
 * OrbLoader — animated loader inspired by Hermes Desktop's OrbLoader.
 * Three orbiting dots around a pulsing core, in the Hermes emerald color.
 */
@Composable
fun OrbLoader(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    color: Color = HermesColors.Primary
) {
    val transition = rememberInfiniteTransition(label = "orb")
    val rotation = transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Restart),
        label = "rotation"
    )
    val pulse = transition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "pulse"
    )
    Canvas(modifier = modifier.size(size)) {
        val cx = size.toPx() / 2f
        val cy = size.toPx() / 2f
        val r = size.toPx() / 2f
        rotate(rotation.value) {
            val dotR = r * 0.18f
            val orbit = r * 0.62f
            val angles = listOf(0f, 120f, 240f)
            for (a in angles) {
                val rad = Math.toRadians(a.toDouble())
                val x = cx + orbit * kotlin.math.cos(rad).toFloat()
                val y = cy + orbit * kotlin.math.sin(rad).toFloat()
                drawCircle(color = color, radius = dotR, center = Offset(x, y))
            }
        }
        drawCircle(
            color = color,
            radius = r * 0.28f * pulse.value,
            center = Offset(cx, cy)
        )
    }
}
