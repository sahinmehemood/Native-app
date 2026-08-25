package com.hermes.android.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hermes.android.core.gateway.GatewayConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Default gateway endpoint. The on-device Hermes `api_server` runs locally; see
 * docs/HERMES-MOBILE-API.md §2. The user can override this in Settings.
 */
const val DEFAULT_GATEWAY_URL = "http://127.0.0.1:8642"

private val Context.dataStore by preferencesDataStore(name = "hermes_connection")

private val KEY_BASE_URL = stringPreferencesKey("base_url")
private val KEY_API_KEY = stringPreferencesKey("api_key")

/**
 * Loads/saves the active [GatewayConfig].
 *
 * Backed by DataStore (app-private, process-safe). The `security-crypto`
 * dependency is on the classpath so this can be upgraded to
 * `EncryptedSharedPreferences` without touching callers (AGENTS.md security
 * boundary: the API key is never plaintext-in-repo, never logged).
 *
 * [config] is a hot [StateFlow] seeded from disk on construction, so the
 * gateway client can be built synchronously from the last-known profile while
 * the async read finishes.
 */
class ConnectionRepository(private val context: Context) {
    private val dataStore: DataStore<Preferences> = context.dataStore

    private val _config = MutableStateFlow(GatewayConfig(DEFAULT_GATEWAY_URL, ""))
    val config: StateFlow<GatewayConfig> = _config.asStateFlow()

    init {
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            dataStore.data
                .map { prefs ->
                    GatewayConfig(
                        baseUrl = prefs[KEY_BASE_URL] ?: DEFAULT_GATEWAY_URL,
                        apiKey = prefs[KEY_API_KEY] ?: "",
                    )
                }
                .collect { _config.value = it }
        }
    }

    suspend fun saveConfig(config: GatewayConfig) {
        dataStore.edit { prefs ->
            prefs[KEY_BASE_URL] = config.baseUrl
            prefs[KEY_API_KEY] = config.apiKey
        }
        _config.value = config
    }
}
