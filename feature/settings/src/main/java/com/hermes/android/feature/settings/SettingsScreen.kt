package com.hermes.android.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hermes.android.core.design.tokens.HermesColorTokens
import com.hermes.android.core.design.tokens.HermesSpacing
import com.hermes.android.feature.settings.ConnectionTest
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(onNavigateUp: () -> Unit) {
    val viewModel: SettingsViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Settings") },
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
        )
        Column(
            Modifier.fillMaxSize().padding(HermesSpacing.Lg).weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(HermesSpacing.Lg),
        ) {
            GatewaySection(state, viewModel)
            DiagnosticsSection(state)
        }
    }
}

@Composable
private fun GatewaySection(
    state: SettingsUiState,
    viewModel: SettingsViewModel,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(HermesSpacing.CardRadius),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.fillMaxWidth().padding(HermesSpacing.Lg)) {
            Text("Gateway", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(HermesSpacing.Md))
            OutlinedTextField(
                value = state.host,
                onValueChange = viewModel::onHostChange,
                label = { Text("Gateway host") },
                placeholder = { Text("http://127.0.0.1:8642") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(HermesSpacing.Md))
            OutlinedTextField(
                value = state.apiKey,
                onValueChange = viewModel::onApiKeyChange,
                label = { Text("API key") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(HermesSpacing.Md))
            Row(horizontalArrangement = Arrangement.spacedBy(HermesSpacing.Sm)) {
                Button(
                    onClick = viewModel::save,
                    modifier = Modifier.weight(1f),
                ) { Text("Save") }
                OutlinedButton(onClick = viewModel::testConnection) {
                    if (state.test == ConnectionTest.Testing) {
                        CircularProgressIndicator(Modifier.fillMaxWidth(0.5f))
                    } else {
                        Text("Test connection")
                    }
                }
            }
            if (state.saved) {
                Spacer(Modifier.height(HermesSpacing.Sm))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(HermesSpacing.Sm))
                    Text("Settings saved", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                }
            }
            state.saveError?.let { error ->
                Spacer(Modifier.height(HermesSpacing.Sm))
                Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
            state.testDetail?.let { detail ->
                if (state.test != ConnectionTest.Idle && state.test != ConnectionTest.Testing) {
                    Spacer(Modifier.height(HermesSpacing.Sm))
                    Text(detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun DiagnosticsSection(state: SettingsUiState) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(HermesSpacing.CardRadius),
        tonalElevation = com.hermes.android.core.design.tokens.HermesElevation.Low,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.fillMaxWidth().padding(HermesSpacing.Lg)) {
            Text("Diagnostics", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(HermesSpacing.Md))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Gateway version", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.weight(1f))
                Text(
                    state.gatewayVersion ?: "Unknown",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(HermesSpacing.Sm))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Connection", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.weight(1f))
                ConnectionBadge(state.test)
            }
        }
    }
}

@Composable
private fun ConnectionBadge(test: ConnectionTest) {
    val (label, color) = when (test) {
        ConnectionTest.Idle -> "Not tested" to MaterialTheme.colorScheme.onSurfaceVariant
        ConnectionTest.Testing -> "Testing…" to MaterialTheme.colorScheme.onSurfaceVariant
        ConnectionTest.Online -> "Online" to HermesColorTokens.Dark.statusDone
        ConnectionTest.Degraded -> "Degraded" to HermesColorTokens.Dark.statusAwaiting
        ConnectionTest.Offline -> "Offline" to HermesColorTokens.Dark.statusError
        ConnectionTest.Error -> "Error" to HermesColorTokens.Dark.statusError
    }
    Surface(color = color.copy(alpha = 0.15f), shape = androidx.compose.foundation.shape.RoundedCornerShape(HermesSpacing.ChipRadius)) {
        Row(Modifier.padding(HermesSpacing.Sm), verticalAlignment = Alignment.CenterVertically) {
            val icon = when (test) {
                ConnectionTest.Online -> Icons.Outlined.CheckCircle
                ConnectionTest.Degraded -> Icons.Outlined.Warning
                ConnectionTest.Offline -> Icons.Outlined.CloudOff
                else -> Icons.Outlined.Warning
            }
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(HermesSpacing.Md))
            Spacer(Modifier.width(HermesSpacing.Xs))
            Text(label, color = color, style = MaterialTheme.typography.labelMedium)
        }
    }
}
