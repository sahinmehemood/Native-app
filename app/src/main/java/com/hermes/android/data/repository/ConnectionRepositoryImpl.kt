package com.hermes.android.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hermes.android.domain.model.ConnectionConfig
import com.hermes.android.domain.repository.ConnectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

private val Context.dataStore by preferencesDataStore(name = "hermes_connection")

@Singleton
class ConnectionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ConnectionRepository {
    private val key = stringPreferencesKey("connection_config")
    private val json = Json { ignoreUnknownKeys = true }

    override fun observe(): Flow<ConnectionConfig?> =
        context.dataStore.data.map { prefs ->
            prefs[key]?.let { runCatching { json.decodeFromString(ConnectionConfig.serializer(), it) }.getOrNull() }
        }

    override suspend fun save(config: ConnectionConfig) {
        context.dataStore.edit { it[key] = json.encodeToString(ConnectionConfig.serializer(), config) }
    }

    override suspend fun clear() {
        context.dataStore.edit { it.remove(key) }
    }
}
