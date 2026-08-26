package com.hermes.android.feature.nous.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hermes.android.core.design.tokens.HermesElevation
import com.hermes.android.core.design.tokens.HermesSpacing
import com.hermes.android.core.gateway.model.SessionSummary
import com.hermes.android.core.ui.states.EmptyState
import com.hermes.android.core.ui.states.ErrorState
import com.hermes.android.core.ui.states.LoadingState
import com.hermes.android.core.ui.states.OfflineBanner
import com.hermes.android.feature.nous.viewmodel.NousCaptureStatus
import com.hermes.android.feature.nous.viewmodel.NousSearchStatus
import com.hermes.android.feature.nous.viewmodel.NousUiState
import com.hermes.android.feature.nous.viewmodel.NousViewModel
import com.hermes.android.feature.nous.viewmodel.filteredSessions
import org.koin.androidx.compose.koinViewModel

@Composable
fun NousRoute(
    onNavigateUp: () -> Unit,
    onOpenSession: (String) -> Unit,
) {
    val viewModel: NousViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    NousScreen(
        state = state,
        onNavigateUp = onNavigateUp,
        onOpenSession = onOpenSession,
        onCaptureTextChange = viewModel::onCaptureTextChange,
        onTargetChange = viewModel::onTargetChange,
        onCapture = viewModel::capture,
        onQueryChange = viewModel::onQueryChange,
        onRefresh = viewModel::loadSessions,
        onReconnect = viewModel::onReconnect,
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun NousScreen(
    state: NousUiState,
    onNavigateUp: () -> Unit,
    onOpenSession: (String) -> Unit,
    onCaptureTextChange: (String) -> Unit,
    onTargetChange: (String?) -> Unit,
    onCapture: () -> Unit,
    onQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onReconnect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("NOUS") },
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Refresh")
                }
            },
        )
        OfflineBanner(connection = state.connection, onReconnect = onReconnect)
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = HermesSpacing.Lg),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = HermesSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(HermesSpacing.Lg),
        ) {
            item { CaptureSection(state, onCaptureTextChange, onTargetChange, onCapture, onOpenSession) }
            item { SearchSection(state, onQueryChange, onOpenSession, onReconnect) }
        }
    }
}

@Composable
private fun CaptureSection(
    state: NousUiState,
    onCaptureTextChange: (String) -> Unit,
    onTargetChange: (String?) -> Unit,
    onCapture: () -> Unit,
    onOpenSession: (String) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(HermesSpacing.CardRadius),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.fillMaxWidth().padding(HermesSpacing.Lg)) {
            Text("Capture", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(HermesSpacing.Sm))
            Text(
                "Save a note or link into your Hermes knowledge. It is posted to a session you choose (or a new one) via the gateway.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(HermesSpacing.Md))
            OutlinedTextField(
                value = state.captureText,
                onValueChange = onCaptureTextChange,
                label = { Text("Note or link") },
                placeholder = { Text("e.g. Remember to review the Q3 report") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(HermesSpacing.Md))
            Text("Save to", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(HermesSpacing.Xs))
            Row(horizontalArrangement = Arrangement.spacedBy(HermesSpacing.Sm)) {
                FilterChip(
                    selected = state.targetSessionId == null,
                    onClick = { onTargetChange(null) },
                    label = { Text("New session") },
                )
                state.sessions.take(6).forEach { session ->
                    FilterChip(
                        selected = state.targetSessionId == session.id,
                        onClick = { onTargetChange(session.id) },
                        label = { Text(session.title ?: "Untitled") },
                    )
                }
            }
            Spacer(Modifier.height(HermesSpacing.Md))
            when (state.captureStatus) {
                NousCaptureStatus.Sending -> CircularProgressIndicator(Modifier.fillMaxWidth(0.2f))
                NousCaptureStatus.Error -> Text(
                    state.captureError ?: "Capture failed",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
                NousCaptureStatus.Success -> Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(HermesSpacing.Sm))
                    Text("Saved to ${state.lastCapturedTitle ?: "session"}", style = MaterialTheme.typography.bodyMedium)
                    state.lastCapturedSessionId?.let { id ->
                        Spacer(Modifier.width(HermesSpacing.Sm))
                        Button(onClick = { onOpenSession(id) }) { Text("Open") }
                    }
                }
                NousCaptureStatus.Idle -> Unit
            }
            Spacer(Modifier.height(HermesSpacing.Md))
            Button(
                onClick = onCapture,
                enabled = state.captureText.isNotBlank() && state.captureStatus != NousCaptureStatus.Sending,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Capture") }
        }
    }
}

@Composable
private fun SearchSection(
    state: NousUiState,
    onQueryChange: (String) -> Unit,
    onOpenSession: (String) -> Unit,
    onReconnect: () -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Text("Search your sessions", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(HermesSpacing.Sm))
        Text(
            "Filters your Hermes sessions by title. This searches what Hermes already knows — it is not a separate vault.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(HermesSpacing.Md))
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            label = { Text("Search sessions") },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(HermesSpacing.Md))
        when (state.searchStatus) {
            NousSearchStatus.Loading -> LoadingState()
            NousSearchStatus.Offline -> ErrorState("You are offline — search unavailable", onRetry = onReconnect)
            NousSearchStatus.Error -> ErrorState(state.error ?: "Failed to load", onRetry = onReconnect)
            NousSearchStatus.Empty -> EmptyState("No sessions yet to search.")
            else -> {
                val results = state.filteredSessions()
                if (results.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(HermesSpacing.Lg), contentAlignment = Alignment.Center) {
                        Text(
                            if (state.query.isBlank()) "No sessions yet" else "No sessions match “${state.query}”",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(HermesSpacing.Sm)) {
                        results.take(50).forEach { session -> SearchRow(session, onOpenSession) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchRow(session: SessionSummary, onOpenSession: (String) -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(HermesSpacing.CardRadius),
        tonalElevation = HermesElevation.Low,
        modifier = Modifier.fillMaxWidth().clickable { onOpenSession(session.id) },
    ) {
        Column(Modifier.fillMaxWidth().padding(HermesSpacing.Md)) {
            Text(session.title ?: "Untitled session", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(HermesSpacing.Xs))
            Text("${session.messageCount} messages", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
