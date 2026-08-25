package com.hermes.android.feature.activity

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import com.hermes.android.core.ui.states.EmptyState
import com.hermes.android.core.ui.states.HermesScaffold

@Composable
fun ActivityScreen(onNavigateUp: () -> Unit = {}) {
    HermesScaffold(title = "Activity", onNavigateUp = onNavigateUp) {
        EmptyState("Activity — coming soon")
    }
}
