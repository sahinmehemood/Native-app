package com.hermes.android.presentation.state

import com.hermes.android.domain.model.Message

sealed interface ChatUiState {
    data object Loading : ChatUiState
    data class Ready(
        val messages: List<Message>,
        val streaming: Boolean,
        val input: String,
        val agentName: String?,
        val tokensIn: Int,
        val tokensOut: Int
    ) : ChatUiState
    data class Error(val message: String) : ChatUiState
}
