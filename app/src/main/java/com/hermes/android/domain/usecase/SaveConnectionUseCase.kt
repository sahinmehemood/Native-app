package com.hermes.android.domain.usecase

import com.hermes.android.domain.model.ConnectionConfig
import com.hermes.android.domain.repository.ConnectionRepository
import javax.inject.Inject

class SaveConnectionUseCase @Inject constructor(
    private val repository: ConnectionRepository
) {
    suspend operator fun invoke(config: ConnectionConfig) = repository.save(config)
}
