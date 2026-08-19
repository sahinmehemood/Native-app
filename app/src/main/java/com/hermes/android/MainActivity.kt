package com.hermes.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.hermes.android.presentation.ui.components.HermesBrand
import com.hermes.android.presentation.ui.components.OrbLoader
import com.hermes.android.presentation.ui.theme.HermesColors
import com.hermes.android.presentation.ui.theme.HermesShapes
import com.hermes.android.presentation.ui.theme.HermesSpacing
import com.hermes.android.presentation.ui.theme.HermesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HermesTheme(mode = com.hermes.android.presentation.ui.theme.HermesThemeMode.DARK) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SplashContent()
                }
            }
        }
    }
}

@Composable
fun SplashContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(HermesSpacing.Spacing24),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = HermesShapes.Large,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(HermesSpacing.Spacing24)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(HermesSpacing.Spacing32)
            ) {
                HermesBrand(size = 64)
                Spacer(modifier = Modifier.height(HermesSpacing.Spacing24))
                OrbLoader(size = 40.dp)
                Spacer(modifier = Modifier.height(HermesSpacing.Spacing16))
                Text(
                    text = "Loading your workspace…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HermesColors.OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
