package com.hermes.android.feature.nous

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import com.hermes.android.core.ui.states.EmptyState
import com.hermes.android.core.ui.states.HermesScaffold

@Composable
fun NousScreen(onNavigateUp: () -> Unit = {}) {
    HermesScaffold(title = "NOUS", onNavigateUp = onNavigateUp) {
        EmptyState("NOUS — coming soon")
    }
}
