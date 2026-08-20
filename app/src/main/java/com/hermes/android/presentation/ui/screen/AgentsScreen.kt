package com.hermes.android.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hermes.android.presentation.state.AgentsUiState
import com.hermes.android.presentation.ui.components.HermesEmptyState
import com.hermes.android.presentation.ui.components.HermesErrorState
import com.hermes.android.presentation.ui.components.HermesListItem
import com.hermes.android.presentation.ui.components.OrbLoader
import com.hermes.android.presentation.ui.theme.HermesColors
import com.hermes.android.presentation.ui.theme.HermesShapes
import com.hermes.android.presentation.ui.theme.HermesSpacing
import com.hermes.android.presentation.viewmodel.AgentsViewModel

@Composable
fun AgentsScreen(viewModel: AgentsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agents") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        when (val s = state) {
            is AgentsUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                OrbLoader(40.dp)
            }
            is AgentsUiState.Empty -> Box(Modifier.fillMaxSize().padding(padding)) {
                HermesEmptyState("No agents", "Connect a backend to load agents.")
            }
            is AgentsUiState.Error -> Box(Modifier.fillMaxSize().padding(padding)) {
                HermesErrorState(s.message)
            }
            is AgentsUiState.Success -> LazyColumn(
                Modifier.fillMaxSize().padding(padding).padding(horizontal = HermesSpacing.Spacing12)
            ) {
                items(s.agents, key = { it.id }) { agent ->
                    HermesListItem(
                        title = agent.name,
                        subtitle = agent.description,
                        leading = { AgentAvatar(agent.name, agent.isActive) },
                        trailing = if (agent.isActive) {
                            { Text("Active", color = HermesColors.Primary, style = MaterialTheme.typography.labelLarge) }
                        } else null,
                        onClick = { viewModel.select(agent.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AgentAvatar(name: String, active: Boolean) {
    val bg = if (active) HermesColors.PrimaryContainer else HermesColors.SurfaceVariant
    val fg = if (active) HermesColors.OnPrimaryContainer else HermesColors.OnSurfaceVariant
    Surface(shape = HermesShapes.Medium, color = bg, modifier = Modifier.size(40.dp)) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                name.firstOrNull()?.uppercase() ?: "?",
                color = fg,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
