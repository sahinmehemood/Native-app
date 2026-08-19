package com.hermes.android.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hermes.android.presentation.ui.theme.HermesColors
import com.hermes.android.presentation.ui.theme.HermesShapes
import com.hermes.android.presentation.ui.theme.HermesSpacing

/**
 * Hermes brand mark — a rounded emerald tile with the "H" glyph, plus wordmark.
 */
@Composable
fun HermesBrand(
    modifier: Modifier = Modifier,
    showWordmark: Boolean = true,
    size: Int = 48
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(HermesSpacing.Spacing12)
    ) {
        Surface(
            modifier = Modifier
                .size(size.dp)
                .clip(RoundedCornerShape(14.dp)),
            color = HermesColors.Primary
        ) {
            Column(
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "H",
                    color = HermesColors.OnPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size * 0.55).sp,
                    lineHeight = (size * 0.55).sp
                )
            }
        }
        if (showWordmark) {
            Text(
                text = "Hermes",
                color = HermesColors.OnSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Text(
                text = "Agent",
                color = HermesColors.TextMuted,
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp
            )
        }
    }
}
