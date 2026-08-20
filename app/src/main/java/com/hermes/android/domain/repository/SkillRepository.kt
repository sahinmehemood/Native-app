package com.hermes.android.domain.repository

import com.hermes.android.domain.model.Skill
import kotlinx.coroutines.flow.Flow

interface SkillRepository {
    fun observeSkills(): Flow<List<Skill>>
    suspend fun toggleInstalled(skillId: String): Boolean
}
