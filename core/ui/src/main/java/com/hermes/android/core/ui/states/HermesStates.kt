package com.hermes.android.core.ui.states

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hermes.android.core.design.tokens.HermesSpacing
import com.hermes.android.core.gateway.ConnectionStatus

/**
 * Centered indeterminate progress indicator. Shown while a screen's primary
 * load is in flight (per AGENTS.md: every screen handles a loading state).
 */
@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.size(HermesSpacing.Xl))
    }
}

/**
 * Full-screen error surface with a retry affordance. `onRetry` is always
 * surfaced because the contract forbids silent auto-retry of destructive
 * actions (docs/HERMES-MOBILE-API.md §5).
 */
@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = HermesSpacing.Lg),
            )
            Button(onClick = onRetry, modifier = Modifier.padding(top = HermesSpacing.Sm)) {
                Text("Retry")
            }
        }
    }
}

/** Centered empty-state message (no data, no error). */
@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(HermesSpacing.Lg),
        )
    }
}

/**
 * Connection status strip. Only visible when the gateway is not healthy, so it
 * can be dropped into any screen (or the app shell) without affecting layout
 * when everything is fine. Driven by [ConnectionStatus] from core:gateway.
 */
@Composable
fun OfflineBanner(
    connection: ConnectionStatus,
    onReconnect: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    if (connection == ConnectionStatus.Connected) return
    val text = if (connection == ConnectionStatus.Reconnecting) {
        "Reconnecting to gateway…"
    } else {
        "You are offline"
    }
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(HermesSpacing.Md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onReconnect) { Text("Retry") }
        }
    }
}

/**
 * Minimal shared screen scaffold: branded top bar (optional back affordance)
 * plus a content slot receiving [PaddingValues]. Connection/offline handling is
 * intentionally left to [OfflineBanner] so callers control placement.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HermesScaffold(
    title: String,
    modifier: Modifier = Modifier,
    onNavigateUp: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    Column(modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(title) },
            navigationIcon = if (onNavigateUp != null) {
                {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            } else {
                {}
            },
        )
        Box(Modifier.fillMaxSize().padding(HermesSpacing.Md)) {
            content(PaddingValues(HermesSpacing.None))
        }
    }
}
