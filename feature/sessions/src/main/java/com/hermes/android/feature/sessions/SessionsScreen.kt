package com.hermes.android.feature.sessions

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import com.hermes.android.core.ui.states.EmptyState
import com.hermes.android.core.ui.states.HermesScaffold

@Composable
fun SessionsScreen(onNavigateUp: () -> Unit = {}) {
    HermesScaffold(title = "Sessions", onNavigateUp = onNavigateUp) {
        EmptyState("Sessions — coming soon")
    }
}
