package com.hermes.android.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hermes.android.domain.model.ConnectionMode
import com.hermes.android.presentation.ui.components.HermesChip
import com.hermes.android.presentation.ui.components.HermesListItem
import com.hermes.android.presentation.ui.components.HermesSectionHeader
import com.hermes.android.presentation.ui.theme.HermesSpacing
import com.hermes.android.presentation.ui.theme.HermesThemeMode
import com.hermes.android.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val connection by viewModel.connection.collectAsStateWithLifecycle(initialValue = null)
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle(initialValue = HermesThemeMode.DARK)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { androidx.compose.material3.Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            HermesSectionHeader(index = "//01", title = "Connection")
            if (connection != null) {
                HermesListItem(
                    title = if (connection!!.mode == ConnectionMode.LOCAL) "Local (Termux)" else "Remote API",
                    subtitle = connection!!.baseUrl
                )
            } else {
                HermesListItem(title = "Not configured", subtitle = "Using offline simulation")
            }
            Spacer(Modifier.height(HermesSpacing.Spacing16))

            HermesSectionHeader(index = "//02", title = "Appearance")
            Row(
                Modifier.fillMaxWidth().padding(horizontal = HermesSpacing.Spacing12).horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(HermesSpacing.Spacing8)
            ) {
                HermesChip("Dark", selected = themeMode == HermesThemeMode.DARK, onClick = { viewModel.setThemeMode(HermesThemeMode.DARK) })
                HermesChip("AMOLED", selected = themeMode == HermesThemeMode.AMOLED, onClick = { viewModel.setThemeMode(HermesThemeMode.AMOLED) })
                HermesChip("Light", selected = themeMode == HermesThemeMode.LIGHT, onClick = { viewModel.setThemeMode(HermesThemeMode.LIGHT) })
                HermesChip("System", selected = themeMode == HermesThemeMode.SYSTEM, onClick = { viewModel.setThemeMode(HermesThemeMode.SYSTEM) })
            }
            Spacer(Modifier.height(HermesSpacing.Spacing16))

            HermesSectionHeader(index = "//03", title = "About")
            HermesListItem(title = "Hermes Agent", subtitle = "Version 0.2.0 · Build 2")
            HermesListItem(title = "Backend", subtitle = "Local Termux + Remote API (Ktor/SSE)")
        }
    }
}
