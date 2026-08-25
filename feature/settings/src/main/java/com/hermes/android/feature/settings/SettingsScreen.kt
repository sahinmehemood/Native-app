package com.hermes.android.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hermes.android.core.design.tokens.HermesSpacing
import com.hermes.android.core.ui.states.HermesScaffold
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(onNavigateUp: () -> Unit) {
    val viewModel: SettingsViewModel = koinViewModel()
    val host by viewModel.host.collectAsStateWithLifecycle()
    val apiKey by viewModel.apiKey.collectAsStateWithLifecycle()

    HermesScaffold(title = "Settings", onNavigateUp = onNavigateUp) {
        Column(Modifier.fillMaxSize().padding(HermesSpacing.Lg)) {
            Text("Gateway", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = host,
                onValueChange = viewModel::onHostChange,
                label = { Text("Gateway host") },
                placeholder = { Text("http://127.0.0.1:8642") },
                modifier = Modifier.fillMaxWidth().padding(top = HermesSpacing.Md),
            )
            OutlinedTextField(
                value = apiKey,
                onValueChange = viewModel::onApiKeyChange,
                label = { Text("API key") },
                modifier = Modifier.fillMaxWidth().padding(top = HermesSpacing.Md),
            )
            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth().padding(top = HermesSpacing.Lg),
            ) { Text("Save") }
        }
    }
}
