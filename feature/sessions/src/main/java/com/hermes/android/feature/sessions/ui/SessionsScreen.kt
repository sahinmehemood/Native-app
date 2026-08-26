package com.hermes.android.feature.sessions.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hermes.android.core.design.tokens.HermesElevation
import com.hermes.android.core.design.tokens.HermesSpacing
import com.hermes.android.core.gateway.model.SessionSummary
import com.hermes.android.core.ui.states.EmptyState
import com.hermes.android.core.ui.states.ErrorState
import com.hermes.android.core.ui.states.LoadingState
import com.hermes.android.core.ui.states.OfflineBanner
import com.hermes.android.feature.sessions.viewmodel.SessionsStatus
import com.hermes.android.feature.sessions.viewmodel.SessionsUiState
import com.hermes.android.feature.sessions.viewmodel.SessionsViewModel
import com.hermes.android.feature.sessions.viewmodel.filtered

@Composable
fun SessionsRoute(
    onNavigateUp: () -> Unit,
    onSessionClick: (String) -> Unit,
) {
    val viewModel: SessionsViewModel = org.koin.androidx.compose.koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    SessionsScreen(
        state = state,
        onNavigateUp = onNavigateUp,
        onSessionClick = onSessionClick,
        onQueryChange = viewModel::onQueryChange,
        onRefresh = viewModel::load,
        onDelete = viewModel::deleteSession,
        onReconnect = viewModel::onReconnect,
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SessionsScreen(
    state: SessionsUiState,
    onNavigateUp: () -> Unit,
    onSessionClick: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onDelete: (String) -> Unit,
    onReconnect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Sessions") },
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Refresh sessions")
                }
            },
        )
        OfflineBanner(connection = state.connection, onReconnect = onReconnect)
        when (state.status) {
            SessionsStatus.Loading -> LoadingState(Modifier.fillMaxSize())
            SessionsStatus.Error -> ErrorState(state.error ?: "Failed to load", onRetry = onReconnect, Modifier.fillMaxSize())
            SessionsStatus.Offline -> ErrorState("You are offline — sessions unavailable", onRetry = onReconnect, Modifier.fillMaxSize())
            SessionsStatus.Empty -> EmptyState("No sessions yet. Start a conversation in Hermes to see it here.")
            SessionsStatus.Ready -> SessionsContent(
                state = state,
                onSessionClick = onSessionClick,
                onQueryChange = onQueryChange,
                onDelete = onDelete,
            )
        }
    }
}

@Composable
private fun SessionsContent(
    state: SessionsUiState,
    onSessionClick: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onDelete: (String) -> Unit,
) {
    val items = state.filtered()
    Column(Modifier.fillMaxSize().padding(horizontal = HermesSpacing.Lg)) {
        OutlinedTextField(
            value = state.query,
            onValueChange = onQueryChange,
            label = { Text("Search sessions") },
            placeholder = { Text("Filter by title") },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(bottom = HermesSpacing.Md),
        )
        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    if (state.query.isBlank()) "No sessions yet" else "No sessions match “${state.query}”",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = HermesSpacing.Lg),
                verticalArrangement = Arrangement.spacedBy(HermesSpacing.Sm),
            ) {
                items(items, key = { it.id }) { session ->
                    SessionRow(
                        session = session,
                        onClick = { onSessionClick(session.id) },
                        onDelete = { onDelete(session.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionRow(
    session: SessionSummary,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(HermesSpacing.CardRadius),
        tonalElevation = HermesElevation.Low,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(HermesSpacing.Md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    session.title ?: "Untitled session",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(Modifier.height(HermesSpacing.Xs))
                Text(
                    "${session.messageCount} messages",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete session")
            }
        }
    }
}
