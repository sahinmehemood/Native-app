package com.hermes.android.presentation.state

import com.hermes.android.domain.model.Agent

sealed interface AgentsUiState {
    data object Loading : AgentsUiState
    data object Empty : AgentsUiState
    data class Success(val agents: List<Agent>) : AgentsUiState
    data class Error(val message: String) : AgentsUiState
}
