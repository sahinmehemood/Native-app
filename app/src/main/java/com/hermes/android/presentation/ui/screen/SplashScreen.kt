package com.hermes.android.presentation.ui.screen

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hermes.android.presentation.navigation.Routes
import com.hermes.android.presentation.ui.components.HermesBrand
import com.hermes.android.presentation.ui.components.OrbLoader
import com.hermes.android.presentation.ui.theme.HermesColors
import com.hermes.android.presentation.ui.theme.HermesShapes
import com.hermes.android.presentation.ui.theme.HermesSpacing
import com.hermes.android.presentation.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    val viewModel: SplashViewModel = hiltViewModel()
    val configured by viewModel.configured.collectAsStateWithLifecycle()

    LaunchedEffect(configured) {
        val c = configured ?: return@LaunchedEffect
        delay(1100)
        navController.navigate(if (c) Routes.Main.route else Routes.Setup.route) {
            popUpTo(Routes.Splash.route) { inclusive = true }
        }
    }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.fillMaxSize().padding(HermesSpacing.Spacing24),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(shape = HermesShapes.Large, color = MaterialTheme.colorScheme.surface, modifier = Modifier.padding(HermesSpacing.Spacing24)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(HermesSpacing.Spacing32)
                ) {
                    HermesBrand(size = 64)
                    Spacer(Modifier.height(HermesSpacing.Spacing24))
                    OrbLoader(size = 40.dp)
                    Spacer(Modifier.height(HermesSpacing.Spacing16))
                    Text(
                        "Loading your workspace…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HermesColors.OnSurfaceVariant
                    )
                }
            }
        }
    }
}
