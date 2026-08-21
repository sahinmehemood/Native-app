package com.hermes.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hermes.android.core.design.theme.HermesTheme
import com.hermes.android.core.design.tokens.HermesColorTokens
import com.hermes.android.core.design.tokens.HermesSpacing

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Reduced-motion follows the system accessibility flag; Settings can override later.
        val reduceMotion = resources.configuration.fontScale > 0f &&
                android.provider.Settings.Global.getInt(
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
 * App root. The feature modules (Home/Chat/…) register their destinations into
 * the shared navigation graph under :core:navigation. Until those land, this
 * renders the branded connection shell so the app launches to a real, on-brand
 * screen rather than a blank or hardcoded-colored placeholder.
 */
@Composable
private fun AppRoot() {
    val colors = HermesColorTokens
    var draft by remember { mutableStateOf("") }
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top bar
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HermesSpacing.Lg, vertical = HermesSpacing.Md),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            androidx.compose.material3.Text(
                "Hermes",
                color = colors.Dark.onSurface,
                style = com.hermes.android.core.design.theme.HermesTypography.titleLarge,
            )
            androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
            androidx.compose.material3.IconButton(onClick = {}) {
                androidx.compose.material3.Icon(
                    androidx.compose.material.icons.Icons.Outlined.Settings,
                    "Settings",
                    tint = colors.Dark.onSurfaceMuted,
                )
            }
        }
        // Connection banner (uses semantic tokens, dark theme)
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HermesSpacing.Lg)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(HermesSpacing.CardRadius))
                .background(colors.Dark.SurfaceVariant)
                .padding(HermesSpacing.Lg)
        ) {
            androidx.compose.material3.Text(
                "Gateway not connected — add a Hermes host to begin.",
                color = colors.Dark.OnSurfaceMuted,
            )
        }
        androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
        // Composer stub (real composer ships with feature:chat)
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HermesSpacing.Lg)
        ) {
            androidx.compose.material3.TextField(
                value = draft,
                onValueChange = { draft = it },
                modifier = Modifier.weight(1f),
                placeholder = { androidx.compose.material3.Text("Message Hermes", color = colors.Dark.OnSurfaceMuted) },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(18),
                colors = androidx.compose.material3.TextFieldDefaults.colors(
                    focusedContainerColor = colors.Dark.Surface,
                    unfocusedContainerColor = colors.Dark.Surface,
                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedTextColor = colors.Dark.OnSurface,
                    unfocusedTextColor = colors.Dark.OnSurface,
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
