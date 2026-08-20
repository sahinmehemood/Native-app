package com.hermes.android.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hermes.android.presentation.state.ChatUiState
import com.hermes.android.presentation.ui.components.HermesChatInput
import com.hermes.android.presentation.ui.components.HermesMessageBubble
import com.hermes.android.presentation.ui.theme.HermesColors
import com.hermes.android.presentation.ui.theme.HermesSpacing
import com.hermes.android.presentation.viewmodel.ChatViewModel

@Composable
fun ChatScreen(viewModel: ChatViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Hermes", style = MaterialTheme.typography.titleLarge)
                        val agent = (state as? ChatUiState.Ready)?.agentName
                        if (!agent.isNullOrBlank()) {
                            Text(agent, style = MaterialTheme.typography.bodySmall, color = HermesColors.OnSurfaceVariant)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            val ready = state as? ChatUiState.Ready
            val messages = ready?.messages ?: emptyList()
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = HermesSpacing.Spacing12),
                verticalArrangement = Arrangement.spacedBy(HermesSpacing.Spacing12),
                contentPadding = PaddingValues(vertical = HermesSpacing.Spacing16)
            ) {
                items(messages, key = { it.id }) { msg -> HermesMessageBubble(msg) }
            }
            HorizontalDivider(color = HermesColors.Outline)
            HermesChatInput(
                value = ready?.input ?: "",
                onValueChange = viewModel::onInputChange,
                onSend = viewModel::send,
                enabled = (ready?.streaming ?: false).not()
            )
        }
    }
}
