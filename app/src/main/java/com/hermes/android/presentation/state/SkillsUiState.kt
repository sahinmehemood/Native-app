package com.hermes.android.presentation.state

import com.hermes.android.domain.model.Skill

sealed interface SkillsUiState {
    data object Loading : SkillsUiState
    data object Empty : SkillsUiState
    data class Success(val skills: List<Skill>) : SkillsUiState
    data class Error(val message: String) : SkillsUiState
}
