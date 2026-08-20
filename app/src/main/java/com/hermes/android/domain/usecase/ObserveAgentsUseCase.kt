package com.hermes.android.domain.usecase

import com.hermes.android.domain.model.Agent
import com.hermes.android.domain.repository.AgentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAgentsUseCase @Inject constructor(
    private val repository: AgentRepository
) {
    operator fun invoke(): Flow<List<Agent>> = repository.observeAgents()
}
