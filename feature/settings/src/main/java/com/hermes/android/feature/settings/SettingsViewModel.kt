package com.hermes.android.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.core.data.ConnectionRepository
import com.hermes.android.core.gateway.GatewayConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Edits the active [GatewayConfig]: host + API key. The values are seeded from
 * the [ConnectionRepository] (last-saved profile) and persisted on [save].
 *
 * Per AGENTS.md the API key is never held in the APK and only lands in the
 * encrypted-ready DataStore via [ConnectionRepository.saveConfig].
 */
class SettingsViewModel(private val repository: ConnectionRepository) : ViewModel() {
    private val _host = MutableStateFlow(repository.config.value.baseUrl)
    private val _apiKey = MutableStateFlow(repository.config.value.apiKey)

    val host: StateFlow<String> = _host.asStateFlow()
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    fun onHostChange(value: String) { _host.value = value }
    fun onApiKeyChange(value: String) { _apiKey.value = value }

    fun save() {
        viewModelScope.launch {
            repository.saveConfig(GatewayConfig(baseUrl = _host.value, apiKey = _apiKey.value))
        }
    }
}
