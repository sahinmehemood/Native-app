package com.hermes.android.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.core.data.ConnectionRepository
import com.hermes.android.core.gateway.GatewayConfig
import com.hermes.android.core.gateway.HermesGatewayClient
import com.hermes.android.core.gateway.gatewayClientFor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import java.io.IOException

/** Result of a "Test connection" probe using the currently-typed credentials. */
enum class ConnectionTest { Idle, Testing, Online, Degraded, Offline, Error }

data class SettingsUiState(
    val host: String = "",
    val apiKey: String = "",
    val test: ConnectionTest = ConnectionTest.Idle,
    val testDetail: String? = null,
    /** Live gateway version reported by /v1/capabilities. */
    val gatewayVersion: String? = null,
    val saved: Boolean = false,
    val saveError: String? = null,
)

/**
 * Edits the active [GatewayConfig]: host + API key.
 *
 * On [save] the values are persisted to [ConnectionRepository] (DataStore). The
 * gateway client reads its config from a live [com.hermes.android.core.gateway.GatewayConfigProvider],
 * so the new host/key take effect on the very next request without a process
 * restart. Per AGENTS.md the API key is never held in the APK and only lands in
 * the encrypted-ready DataStore.
 *
 * [testConnection] probes the gateway with the *typed* (not-yet-saved)
 * credentials via `gatewayClientFor`, exercising `/v1/capabilities` and falling
 * back to `/health` to distinguish a reachable-but-unauthorized gateway
 * (Degraded) from an unreachable one (Offline).
 */
class SettingsViewModel(
    private val repository: ConnectionRepository,
    private val gateway: HermesGatewayClient,
) : ViewModel() {

    private val _state = MutableStateFlow(
        SettingsUiState(
            host = repository.config.value.baseUrl,
            apiKey = repository.config.value.apiKey,
        ),
    )
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init { loadLiveVersion() }

    private fun loadLiveVersion() {
        viewModelScope.launch {
            runCatching { gateway.getCapabilities().version }.onSuccess { version ->
                _state.update { it.copy(gatewayVersion = version) }
            }
        }
    }

    fun onHostChange(value: String) {
        _state.update { it.copy(host = value, saved = false, saveError = null) }
    }

    fun onApiKeyChange(value: String) {
        _state.update { it.copy(apiKey = value, saved = false, saveError = null) }
    }

    fun save() {
        viewModelScope.launch {
            val config = GatewayConfig(baseUrl = _state.value.host, apiKey = _state.value.apiKey)
            runCatching { repository.saveConfig(config) }
                .onSuccess {
                    // The gateway client reads its config from a live
                    // GatewayConfigProvider backed by ConnectionRepository, so the
                    // new host/key take effect on the very next request — no module
                    // reload or process restart required.
                    _state.update { it.copy(saved = true, saveError = null) }
                    loadLiveVersion()
                }
                .onFailure { e ->
                    _state.update { it.copy(saveError = e.message ?: "Failed to save settings") }
                }
        }
    }

    fun testConnection() {
        val config = GatewayConfig(baseUrl = _state.value.host, apiKey = _state.value.apiKey)
        _state.update { it.copy(test = ConnectionTest.Testing, testDetail = null) }
        viewModelScope.launch {
            val probe = gatewayClientFor(config)
            runCatching { probe.getCapabilities() }
                .fold(
                    onSuccess = { caps ->
                        _state.update {
                            it.copy(
                                test = ConnectionTest.Online,
                                testDetail = caps.version?.let { v -> "Connected · gateway $v" } ?: "Connected",
                                gatewayVersion = caps.version ?: it.gatewayVersion,
                            )
                        }
                    },
                    onFailure = { e ->
                        // Reachable but unauthorized / capabilities failed → Degraded.
                        if (e is IOException) {
                            _state.update { it.copy(test = ConnectionTest.Offline, testDetail = "Cannot reach gateway") }
                            return@fold
                        }
                        val degraded = runCatching { probe.getHealth() }.isSuccess
                        if (degraded) {
                            _state.update { it.copy(test = ConnectionTest.Degraded, testDetail = "Reachable but authorization failed") }
                        } else {
                            _state.update { it.copy(test = ConnectionTest.Error, testDetail = e.message ?: "Connection test failed") }
                        }
                    },
                )
        }
    }
}
