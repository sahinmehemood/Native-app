package com.hermes.android.domain.usecase

import com.hermes.android.domain.model.Skill
import com.hermes.android.domain.repository.SkillRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSkillsUseCase @Inject constructor(
    private val repository: SkillRepository
) {
    operator fun invoke(): Flow<List<Skill>> = repository.observeSkills()
}
