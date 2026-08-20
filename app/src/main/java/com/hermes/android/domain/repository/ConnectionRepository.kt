package com.hermes.android.domain.repository

import com.hermes.android.domain.model.ConnectionConfig
import kotlinx.coroutines.flow.Flow

interface ConnectionRepository {
    fun observe(): Flow<ConnectionConfig?>
    suspend fun save(config: ConnectionConfig)
    suspend fun clear()
}
