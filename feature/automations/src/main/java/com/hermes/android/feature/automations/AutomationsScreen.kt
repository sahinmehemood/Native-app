package com.hermes.android.feature.automations

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import com.hermes.android.core.ui.states.EmptyState
import com.hermes.android.core.ui.states.HermesScaffold

@Composable
fun AutomationsScreen(onNavigateUp: () -> Unit = {}) {
    HermesScaffold(title = "Automations", onNavigateUp = onNavigateUp) {
        EmptyState("Automations — coming soon")
    }
}
