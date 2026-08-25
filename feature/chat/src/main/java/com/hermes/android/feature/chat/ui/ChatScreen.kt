package com.hermes.android.feature.chat.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hermes.android.core.design.tokens.HermesSpacing
import com.hermes.android.core.gateway.ConnectionStatus
import com.hermes.android.core.ui.states.ErrorState
import com.hermes.android.core.ui.states.OfflineBanner
import com.hermes.android.feature.chat.viewmodel.ChatUiState
import com.hermes.android.feature.chat.viewmodel.ChatViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ChatRoute(sessionId: String) {
    val viewModel: ChatViewModel = koinViewModel(parameters = { parametersOf(sessionId) })
    val state by viewModel.state.collectAsStateWithLifecycle()
    ChatScreen(
        state = state,
        onDraftChange = viewModel::onDraftChange,
        onSend = viewModel::send,
        onApproval = viewModel::onApproval,
        onStop = viewModel::onStop,
        onReconnect = viewModel::onReconnect,
    )
}

@Composable
fun ChatScreen(
    state: ChatUiState,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit,
    onApproval: (String, String?) -> Unit,
    onStop: () -> Unit,
    onReconnect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxSize().padding(HermesSpacing.Md)) {
        OfflineBanner(connection = state.connection, onReconnect = onReconnect)
        val error = state.error
        if (error != null && state.messages.isEmpty()) {
            ErrorState(message = error, onRetry = onReconnect, modifier = Modifier.weight(1f))
        } else {
            MessageList(
                messages = state.messages,
                toolActivity = state.toolActivity,
                modifier = Modifier.weight(1f),
            )
            state.pendingApproval?.let { approval ->
                Spacer(Modifier.height(HermesSpacing.Sm))
                ApprovalCard(approval = approval, onResolve = onApproval, onStop = onStop)
            }
        }
        Composer(
            draft = state.draft,
            onDraftChange = onDraftChange,
            onSend = onSend,
            enabled = !state.isStreaming,
        )
    }
}
