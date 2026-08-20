package com.hermes.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.domain.repository.SkillRepository
import com.hermes.android.domain.usecase.ObserveSkillsUseCase
import com.hermes.android.presentation.state.SkillsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SkillsViewModel @Inject constructor(
    private val observeSkills: ObserveSkillsUseCase,
    private val skillRepository: SkillRepository
) : ViewModel() {
    private val _state = MutableStateFlow<SkillsUiState>(SkillsUiState.Loading)
    val state: StateFlow<SkillsUiState> = _state.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    init {
        viewModelScope.launch {
            observeSkills().collect { skills ->
                _state.value = if (skills.isEmpty()) SkillsUiState.Empty else SkillsUiState.Success(skills)
            }
        }
    }

    fun onQueryChange(q: String) { _query.value = q }

    fun toggle(id: String) {
        viewModelScope.launch { skillRepository.toggleInstalled(id) }
    }
}
