package com.hermes.android.feature.activity.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.hermes.android.core.ui.states.EmptyState
import com.hermes.android.core.ui.states.ErrorState
import com.hermes.android.core.ui.states.LoadingState
import com.hermes.android.core.ui.states.OfflineBanner
import com.hermes.android.feature.activity.viewmodel.ActivityItem
import com.hermes.android.feature.activity.viewmodel.ActivityStatus
import com.hermes.android.feature.activity.viewmodel.ActivityUiState
import com.hermes.android.feature.activity.viewmodel.ActivityViewModel
import com.hermes.android.feature.activity.viewmodel.ItemMessagesStatus
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActivityRoute(
    onNavigateUp: () -> Unit,
    onOpenSession: (String) -> Unit,
) {
    val viewModel: ActivityViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    ActivityScreen(
        state = state,
        onNavigateUp = onNavigateUp,
        onOpenSession = onOpenSession,
        onToggleExpand = viewModel::toggleExpand,
        onRefresh = viewModel::load,
        onReloadMessages = viewModel::reloadMessages,
        onReconnect = viewModel::onReconnect,
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    state: ActivityUiState,
    onNavigateUp: () -> Unit,
    onOpenSession: (String) -> Unit,
    onToggleExpand: (String) -> Unit,
    onRefresh: () -> Unit,
    onReloadMessages: (String) -> Unit,
    onReconnect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Activity") },
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Refresh activity")
                }
            },
        )
        OfflineBanner(connection = state.connection, onReconnect = onReconnect)
        when (state.status) {
            ActivityStatus.Loading -> LoadingState(Modifier.fillMaxSize())
            ActivityStatus.Error -> ErrorState(state.error ?: "Failed to load", onRetry = onReconnect, Modifier.fillMaxSize())
            ActivityStatus.Offline -> ErrorState("You are offline — activity unavailable", onRetry = onReconnect, Modifier.fillMaxSize())
            ActivityStatus.Empty -> EmptyState("No activity yet. Conversations and tool runs will appear here.")
            ActivityStatus.Ready -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = HermesSpacing.Lg),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = HermesSpacing.Lg),
                verticalArrangement = Arrangement.spacedBy(HermesSpacing.Sm),
            ) {
                items(state.items, key = { it.session.id }) { item ->
                    ActivityRow(
                        item = item,
                        onToggleExpand = { onToggleExpand(item.session.id) },
                        onOpenSession = { onOpenSession(item.session.id) },
                        onReloadMessages = { onReloadMessages(item.session.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityRow(
    item: ActivityItem,
    onToggleExpand: () -> Unit,
    onOpenSession: () -> Unit,
    onReloadMessages: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(HermesSpacing.CardRadius),
        tonalElevation = HermesElevation.Low,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.fillMaxWidth().padding(HermesSpacing.Md)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onToggleExpand),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(item.session.title ?: "Untitled session", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(HermesSpacing.Xs))
                    Text(
                        "${item.session.messageCount} messages${item.session.updatedAt?.let { " · $it" } ?: ""}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = onOpenSession) {
                    Icon(Icons.Outlined.ChatBubble, contentDescription = "Open session")
                }
                IconButton(onClick = onToggleExpand) {
                    Icon(
                        if (item.expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                        contentDescription = if (item.expanded) "Collapse" else "Expand",
                    )
                }
            }
            if (item.expanded) {
                Spacer(Modifier.height(HermesSpacing.Sm))
                when (item.messagesStatus) {
                    ItemMessagesStatus.Idle -> Unit
                    ItemMessagesStatus.Loading -> CircularProgressIndicator(Modifier.fillMaxWidth(0.2f).padding(HermesSpacing.Sm))
                    ItemMessagesStatus.Offline -> ErrorState("Offline — messages unavailable", onRetry = onReloadMessages)
                    ItemMessagesStatus.Error -> ErrorState(item.messagesError ?: "Failed", onRetry = onReloadMessages)
                    ItemMessagesStatus.Empty -> Text("No messages yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    ItemMessagesStatus.Ready -> Column(verticalArrangement = Arrangement.spacedBy(HermesSpacing.Xs)) {
                        item.messages.takeLast(8).forEach { message ->
                            Text(
                                "• ${message.role}: ${message.content.take(120)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}
