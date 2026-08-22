package com.hermes.android.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.core.gateway.ConnectionStatus
import com.hermes.android.core.gateway.HermesGatewayClient
import com.hermes.android.core.gateway.PendingApprovalsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import java.io.IOException

class HomeViewModel(
    private val gateway: HermesGatewayClient,
    private val pendingApprovals: PendingApprovalsStore,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()
    val pendingApprovalsCount: StateFlow<Int> = pendingApprovals.count

    init { load() }

    fun load() {
        _state.value = _state.value.copy(
            health = HealthLoad.Loading,
            sessions = SessionsLoad.Loading,
            connection = ConnectionStatus.Connected,
        )
        viewModelScope.launch { loadHealth() }
        viewModelScope.launch { loadSessions() }
    }

    private suspend fun loadHealth() {
        try {
            val health = gateway.getHealth()
            _state.value = _state.value.copy(health = HealthLoad.Ready(health))
        } catch (_: IOException) {
            _state.value = _state.value.copy(health = HealthLoad.Offline)
        } catch (e: Exception) {
            _state.value = _state.value.copy(health = HealthLoad.Error(e.message ?: "Failed"))
        }
    }

    private suspend fun loadSessions() {
        try {
            val sessions = gateway.getSessions()
            _state.value = _state.value.copy(
                sessions = if (sessions.isEmpty()) SessionsLoad.Empty else SessionsLoad.Ready(sessions),
            )
        } catch (_: IOException) {
            _state.value = _state.value.copy(sessions = SessionsLoad.Offline)
        } catch (e: Exception) {
            _state.value = _state.value.copy(sessions = SessionsLoad.Error(e.message ?: "Failed"))
        }
    }

    fun onReconnect() {
        load()
    }
}
