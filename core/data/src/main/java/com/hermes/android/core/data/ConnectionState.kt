package com.hermes.android.core.data

import com.hermes.android.core.gateway.ConnectionStatus
import com.hermes.android.core.gateway.HermesGatewayClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Shared, process-wide gateway connection signal.
 *
 * The app shell (and any screen that wants a banner) injects this and renders
 * [com.hermes.android.core.ui.states.OfflineBanner]. [probe] pings `/health`
 * (docs/HERMES-MOBILE-API.md §3) and is also wired to the banner's retry
 * affordance. Per the contract, reconnect never auto-resends a prompt.
 */
class ConnectionState(private val gateway: HermesGatewayClient) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _status = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Connected)
    val status: StateFlow<ConnectionStatus> = _status.asStateFlow()

    init {
        probe()
    }

    fun probe() {
        scope.launch {
            _status.value = ConnectionStatus.Reconnecting
            _status.value = runCatching { gateway.getHealth() }
                .fold(
                    onSuccess = { ConnectionStatus.Connected },
                    onFailure = { ConnectionStatus.Offline },
                )
        }
    }
}
