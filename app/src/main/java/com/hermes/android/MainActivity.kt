package com.hermes.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.hermes.android.core.design.theme.HermesTheme
import com.hermes.android.core.design.theme.HermesTypography
import com.hermes.android.core.design.tokens.HermesColorTokens
import com.hermes.android.core.design.tokens.HermesSpacing

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Reduced-motion follows the system accessibility flag; Settings can override later.
        val reduceMotion = android.provider.Settings.Global.getInt(
            contentResolver,
            android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
            1,
        ) == 0f
        setContent {
            HermesTheme(darkTheme = isSystemInDarkTheme(), reducedMotion = reduceMotion) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot()
                }
            }
        }
    }
}

/**
 * App root. Feature modules (Home/Chat/…) register destinations into the shared
 * navigation graph under :core:navigation. Until those land, this renders the
 * branded connection shell so the app launches to a real, on-brand screen.
 */
@Composable
private fun AppRoot() {
    val c = HermesColorTokens.Dark
    var draft by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HermesSpacing.Lg, vertical = HermesSpacing.Md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Hermes", color = c.onSurface, style = HermesTypography.titleLarge)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Settings, "Settings", tint = c.onSurfaceMuted)
            }
        }
        // Connection banner (semantic tokens, dark theme)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HermesSpacing.Lg)
                .clip(RoundedCornerShape(HermesSpacing.CardRadius))
                .padding(HermesSpacing.Lg),
        ) {
            Text(
                "Gateway not connected — add a Hermes host to begin.",
                color = c.onSurfaceMuted,
            )
        }
        Spacer(Modifier.weight(1f))
        // Composer stub (real composer ships with feature:chat)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HermesSpacing.Lg),
            verticalAlignment = Alignment.Bottom,
        ) {
            TextField(
                value = draft,
                onValueChange = { value -> draft = value },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message Hermes", color = c.onSurfaceMuted) },
                shape = RoundedCornerShape(18),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = c.surface,
                    unfocusedContainerColor = c.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = c.onSurface,
                    unfocusedTextColor = c.onSurface,
                ),
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun AppRootPreview() {
    HermesTheme(darkTheme = true) { Surface(Modifier.fillMaxSize()) { AppRoot() } }
}
