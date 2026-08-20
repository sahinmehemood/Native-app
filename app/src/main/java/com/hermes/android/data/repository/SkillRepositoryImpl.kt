package com.hermes.android.data.repository

import com.hermes.android.data.api.HermesApiService
import com.hermes.android.data.source.local.MockLocalSource
import com.hermes.android.domain.model.Skill
import com.hermes.android.domain.repository.SkillRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SkillRepositoryImpl @Inject constructor(
    private val api: HermesApiService,
    private val mock: MockLocalSource
) : SkillRepository {
    private val installed = MutableStateFlow(
        mock.getSkills().filter { it.installed }.map { it.id }.toSet()
    )

    override fun observeSkills(): Flow<List<Skill>> = flow {
        val base = try {
            api.getSkills().map { it.toDomain() }
        } catch (_: Exception) {
            mock.getSkills()
        }
        installed.map { set -> base.map { it.copy(installed = set.contains(it.id)) } }
            .collect { emit(it) }
    }

    override suspend fun toggleInstalled(skillId: String): Boolean {
        val cur = installed.value
        val next = if (cur.contains(skillId)) cur - skillId else cur + skillId
        installed.value = next
        return next.contains(skillId)
    }
}
