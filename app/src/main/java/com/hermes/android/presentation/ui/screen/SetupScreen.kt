package com.hermes.android.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hermes.android.domain.model.ConnectionMode
import com.hermes.android.presentation.navigation.Routes
import com.hermes.android.presentation.ui.components.HermesBrand
import com.hermes.android.presentation.ui.components.HermesButton
import com.hermes.android.presentation.ui.components.HermesChip
import com.hermes.android.presentation.ui.components.HermesTextField
import com.hermes.android.presentation.ui.theme.HermesColors
import com.hermes.android.presentation.ui.theme.HermesSpacing
import com.hermes.android.presentation.viewmodel.SetupViewModel

@Composable
fun SetupScreen(navController: NavHostController) {
    val viewModel: SetupViewModel = hiltViewModel()
    val mode by viewModel.mode.collectAsStateWithLifecycle()
    val url by viewModel.url.collectAsStateWithLifecycle()
    val key by viewModel.key.collectAsStateWithLifecycle()
    val saved by viewModel.saved.collectAsStateWithLifecycle()

    LaunchedEffect(saved) {
        if (saved) {
            navController.navigate(Routes.Main.route) {
                popUpTo(Routes.Setup.route) { inclusive = true }
            }
        }
    }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.fillMaxSize().padding(HermesSpacing.Spacing24).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(HermesSpacing.Spacing32))
            HermesBrand(size = 56)
            Spacer(Modifier.height(HermesSpacing.Spacing8))
            Text("Connect Hermes", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(
                "Choose how this app reaches your agent.",
                style = MaterialTheme.typography.bodyMedium,
                color = HermesColors.OnSurfaceVariant
            )
            Spacer(Modifier.height(HermesSpacing.Spacing24))

            Row(horizontalArrangement = Arrangement.spacedBy(HermesSpacing.Spacing8))) {
                HermesChip("Local (Termux)", selected = mode == ConnectionMode.LOCAL, onClick = { viewModel.setMode(ConnectionMode.LOCAL) }, modifier = Modifier.weight(1f))
                HermesChip("Remote API", selected = mode == ConnectionMode.REMOTE, onClick = { viewModel.setMode(ConnectionMode.REMOTE) }, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(HermesSpacing.Spacing16))
            HermesTextField(value = url, onValueChange = viewModel::setUrl, label = "Server URL", placeholder = "http://127.0.0.1:8642")
            Spacer(Modifier.height(HermesSpacing.Spacing12))
            HermesTextField(
                value = key,
                onValueChange = viewModel::setKey,
                label = "API key (optional)",
                placeholder = "sk-…",
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(HermesSpacing.Spacing24))
            HermesButton("Save & Continue", onClick = viewModel::save)
            Spacer(Modifier.height(HermesSpacing.Spacing12))
            Text(
                "You can change this later in Settings. Without a server, the app runs a built-in offline simulation.",
                style = MaterialTheme.typography.bodySmall,
                color = HermesColors.TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}
