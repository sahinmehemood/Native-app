package com.hermes.android.domain.usecase

import com.hermes.android.domain.model.ConnectionConfig
import com.hermes.android.domain.repository.ConnectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConnectionUseCase @Inject constructor(
    private val repository: ConnectionRepository
) {
    operator fun invoke(): Flow<ConnectionConfig?> = repository.observe()
}
