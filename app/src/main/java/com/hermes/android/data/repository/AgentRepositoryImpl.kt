package com.hermes.android.data.repository

import com.hermes.android.data.api.HermesApiService
import com.hermes.android.data.source.local.MockLocalSource
import com.hermes.android.domain.model.Agent
import com.hermes.android.domain.repository.AgentRepository
import com.hermes.android.domain.repository.ConnectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentRepositoryImpl @Inject constructor(
    private val api: HermesApiService,
    private val connectionRepository: ConnectionRepository,
    private val mock: MockLocalSource
) : AgentRepository {
    override fun observeAgents(): Flow<List<Agent>> = flow {
        val base = try {
            api.getProfiles().map { it.toDomain() }
        } catch (_: Exception) {
            mock.getAgents()
        }
        connectionRepository.observe().map { c ->
            val activeId = c?.activeAgentId
            base.map { it.copy(isActive = it.id == activeId) }
        }.collect { emit(it) }
    }

    override suspend fun setActive(agentId: String) {
        val c = connectionRepository.observe().first() ?: return
        connectionRepository.save(c.copy(activeAgentId = agentId))
    }

    override suspend fun getActive(): Agent? {
        val c = connectionRepository.observe().first() ?: return null
        val base = try {
            api.getProfiles().map { it.toDomain() }
        } catch (_: Exception) {
            mock.getAgents()
        }
        return base.firstOrNull { it.id == c.activeAgentId }
    }
}
