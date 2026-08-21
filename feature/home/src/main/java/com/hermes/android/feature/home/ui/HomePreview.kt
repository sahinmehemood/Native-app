package com.hermes.android.feature.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hermes.android.core.design.theme.HermesTheme
import com.hermes.android.core.gateway.model.SessionSummary
import com.hermes.android.feature.home.viewmodel.HomeUiState
import com.hermes.android.feature.home.viewmodel.SessionsLoad

@Preview(name = "Home (Dark)")
@Composable
fun HomePreviewDark() {
    HermesTheme(darkTheme = true) {
        HomeScreen(
            state = HomeUiState(sessions = SessionsLoad.Ready(sampleSessions())),
            pendingApprovals = 2,
            onSessionClick = {},
            onReload = {},
            onReconnect = {},
        )
    }
}

@Preview(name = "Home (Light)", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomePreviewLight() {
    HermesTheme(darkTheme = false) {
        HomeScreen(
            state = HomeUiState(sessions = SessionsLoad.Ready(sampleSessions())),
            pendingApprovals = 2,
            onSessionClick = {},
            onReload = {},
            onReconnect = {},
        )
    }
}

private fun sampleSessions(): List<SessionSummary> = listOf(
    SessionSummary(id = "s1", title = "Log triage", messageCount = 12),
    SessionSummary(id = "s2", title = "Release notes", messageCount = 4),
)
