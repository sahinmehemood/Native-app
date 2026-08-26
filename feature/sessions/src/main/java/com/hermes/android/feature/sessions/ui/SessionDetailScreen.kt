package com.hermes.android.feature.sessions.ui

import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.Refresh
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
import com.hermes.android.core.design.tokens.HermesColorTokens
import com.hermes.android.core.design.tokens.HermesElevation
import com.hermes.android.core.design.tokens.HermesSpacing
import com.hermes.android.core.gateway.model.Message
import com.hermes.android.core.ui.states.EmptyState
import com.hermes.android.core.ui.states.ErrorState
import com.hermes.android.core.ui.states.LoadingState
import com.hermes.android.core.ui.states.OfflineBanner
import com.hermes.android.feature.sessions.viewmodel.SessionDetailStatus
import com.hermes.android.feature.sessions.viewmodel.SessionDetailUiState
import com.hermes.android.feature.sessions.viewmodel.SessionDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SessionDetailRoute(
    sessionId: String,
    onNavigateUp: () -> Unit,
    onOpenInChat: (String) -> Unit,
) {
    val viewModel: SessionDetailViewModel = koinViewModel(parameters = { parametersOf(sessionId) })
    val state by viewModel.state.collectAsStateWithLifecycle()
    SessionDetailScreen(
        state = state,
        onNavigateUp = onNavigateUp,
        onOpenInChat = onOpenInChat,
        onRefresh = viewModel::load,
        onReconnect = viewModel::onReconnect,
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    state: SessionDetailUiState,
    onNavigateUp: () -> Unit,
    onOpenInChat: (String) -> Unit,
    onRefresh: () -> Unit,
    onReconnect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(state.title ?: "Session") },
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { onOpenInChat(state.sessionId) }) {
                    Icon(Icons.Outlined.ChatBubble, contentDescription = "Open in chat")
                }
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Refresh messages")
                }
            },
        )
        OfflineBanner(connection = state.connection, onReconnect = onReconnect)
        when (state.status) {
            SessionDetailStatus.Loading -> LoadingState(Modifier.fillMaxSize())
            SessionDetailStatus.Error -> ErrorState(state.error ?: "Failed to load", onRetry = onReconnect, Modifier.fillMaxSize())
            SessionDetailStatus.Offline -> ErrorState("You are offline — messages unavailable", onRetry = onReconnect, Modifier.fillMaxSize())
            SessionDetailStatus.Empty -> EmptyState("This session has no messages yet.")
            SessionDetailStatus.Ready -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = HermesSpacing.Lg),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = HermesSpacing.Lg),
                verticalArrangement = Arrangement.spacedBy(HermesSpacing.Sm),
            ) {
                items(state.messages, key = { it.id ?: it.createdAt ?: it.content.take(16) }) { message ->
                    MessageRow(message = message)
                }
            }
        }
    }
}

@Composable
private fun MessageRow(message: Message) {
    val isDark = isSystemInDarkTheme()
    val tokens = if (isDark) HermesColorTokens.Dark else HermesColorTokens.Light
    val isUser = message.role == "user"
    val bubbleColor = if (isUser) tokens.accentSoft else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isUser) tokens.accentOn else MaterialTheme.colorScheme.onSurface
    Surface(
        color = bubbleColor,
        shape = RoundedCornerShape(HermesSpacing.CardRadius),
        tonalElevation = HermesElevation.Low,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.fillMaxWidth().padding(HermesSpacing.Md)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = message.role.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isUser) tokens.accent else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                message.createdAt?.let {
                    Spacer(Modifier.width(HermesSpacing.Sm))
                    Text(it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(HermesSpacing.Xs))
            Text(
                text = message.content.ifBlank { "…" },
                color = contentColor,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
