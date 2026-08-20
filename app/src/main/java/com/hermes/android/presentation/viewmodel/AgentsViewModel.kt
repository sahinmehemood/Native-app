package com.hermes.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.domain.usecase.ObserveAgentsUseCase
import com.hermes.android.domain.usecase.SetActiveAgentUseCase
import com.hermes.android.presentation.state.AgentsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgentsViewModel @Inject constructor(
    private val observeAgents: ObserveAgentsUseCase,
    private val setActiveAgent: SetActiveAgentUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<AgentsUiState>(AgentsUiState.Loading)
    val state: StateFlow<AgentsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeAgents().collect { agents ->
                _state.value = if (agents.isEmpty()) AgentsUiState.Empty else AgentsUiState.Success(agents)
            }
        }
    }

    fun select(id: String) {
        viewModelScope.launch { setActiveAgent(id) }
    }
}
