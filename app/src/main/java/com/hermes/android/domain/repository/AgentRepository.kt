package com.hermes.android.domain.repository

import com.hermes.android.domain.model.Agent
import kotlinx.coroutines.flow.Flow

interface AgentRepository {
    fun observeAgents(): Flow<List<Agent>>
    suspend fun setActive(agentId: String)
    suspend fun getActive(): Agent?
}
