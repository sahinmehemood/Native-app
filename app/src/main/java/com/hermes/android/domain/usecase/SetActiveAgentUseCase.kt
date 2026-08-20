package com.hermes.android.domain.usecase

import com.hermes.android.domain.model.ConnectionConfig
import com.hermes.android.domain.repository.ConnectionRepository
import javax.inject.Inject

class SetActiveAgentUseCase @Inject constructor(
    private val repository: ConnectionRepository
) {
    suspend operator fun invoke(agentId: String) {
        val current = repository.observe().firstOrNull() ?: return
        repository.save(current.copy(activeAgentId = agentId))
    }
}
