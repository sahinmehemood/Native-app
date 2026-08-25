package com.hermes.android.feature.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.hermes.android.feature.home.viewmodel.HealthLoad
import com.hermes.android.feature.home.viewmodel.HomeUiState
import com.hermes.android.feature.home.viewmodel.HomeViewModel
import com.hermes.android.feature.home.viewmodel.SessionsLoad
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute(
    onSessionClick: (String) -> Unit,
    onSettingsClick: () -> Unit = {},
) {
    val viewModel: HomeViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pending by viewModel.pendingApprovalsCount.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        pendingApprovals = pending,
        onSessionClick = onSessionClick,
        onSettingsClick = onSettingsClick,
        onReload = viewModel::load,
        onReconnect = viewModel::onReconnect,
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    pendingApprovals: Int,
    onSessionClick: (String) -> Unit,
    onSettingsClick: () -> Unit = {},
    onReload: () -> Unit,
    onReconnect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(HermesSpacing.Lg),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Hermes", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            if (pendingApprovals > 0) PendingApprovalsBadge(count = pendingApprovals)
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings")
            }
        }
        Spacer(Modifier.height(HermesSpacing.Lg))
        HealthTile(load = state.health)
        Spacer(Modifier.height(HermesSpacing.Lg))
        Text("Recent sessions", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(HermesSpacing.Md))
        SessionsList(load = state.sessions, onSessionClick = onSessionClick, onRetry = onReconnect)
    }
}

@Composable
fun HealthTile(load: HealthLoad) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(HermesSpacing.CardRadius),
        tonalElevation = HermesElevation.Low,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(HermesSpacing.Md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (load) {
                HealthLoad.Loading -> CircularProgressIndicator()
                is HealthLoad.Ready -> {
                    val ok = load.status.status == "ok"
                    val color = if (ok) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    Text("Gateway", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    Text(
                        load.status.version ?: "",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Surface(
                        color = color,
                        shape = RoundedCornerShape(HermesSpacing.ChipRadius),
                        modifier = Modifier.padding(start = HermesSpacing.Sm),
                    ) {
                        Text(
                            if (ok) "Online" else "Degraded",
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(HermesSpacing.Sm),
                        )
                    }
                }
                HealthLoad.Offline -> Text(
                    "Gateway offline",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                )
                is HealthLoad.Error -> Text(
                    "Health check failed: ${load.message}",
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
fun PendingApprovalsBadge(count: Int) {
    Surface(
        color = MaterialTheme.colorScheme.error,
        shape = RoundedCornerShape(HermesSpacing.ChipRadius),
    ) {
        Text(
            "$count pending",
            color = MaterialTheme.colorScheme.onError,
            modifier = Modifier.padding(HermesSpacing.Sm),
        )
    }
}

@Composable
fun SessionsList(load: SessionsLoad, onSessionClick: (String) -> Unit, onRetry: () -> Unit) {
    when (load) {
        SessionsLoad.Loading -> Box(
            Modifier.fillMaxWidth().height(HermesSpacing.Xxl),
            contentAlignment = Alignment.Center,
        ) { CircularProgressIndicator() }
        SessionsLoad.Empty -> EmptyState("No recent sessions yet")
        SessionsLoad.Offline -> ErrorState("Offline — sessions unavailable", onRetry)
        is SessionsLoad.Error -> ErrorState(load.message, onRetry)
        is SessionsLoad.Ready -> LazyColumn {
            items(load.sessions, key = { it.id }) { session ->
                SessionItem(session = session, onClick = { onSessionClick(session.id) })
            }
        }
    }
}

@Composable
fun SessionItem(session: SessionSummary, onClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(HermesSpacing.CardRadius),
        tonalElevation = HermesElevation.Low,
        modifier = Modifier.fillMaxWidth().padding(vertical = HermesSpacing.Xs).clickable(onClick = onClick),
    ) {
        Column(Modifier.fillMaxWidth().padding(HermesSpacing.Md)) {
            Text(session.title ?: "Untitled session", style = MaterialTheme.typography.bodyLarge)
            Text(
                "${session.messageCount} messages",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
