package com.hermes.android.core.data

import com.hermes.android.core.gateway.HermesGatewayClient
import com.hermes.android.core.gateway.model.HealthStatus
import com.hermes.android.core.gateway.model.SessionSummary

/**
 * Thin domain wrapper over [HermesGatewayClient] for the Home/Settings screens.
 *
 * Centralizing the call here keeps feature ViewModels free of transport
 * concerns and gives us one place to add caching/retry later without changing
 * the contract (docs/HERMES-MOBILE-API.md §3).
 */
class SessionRepository(private val gateway: HermesGatewayClient) {
    suspend fun getSessions(): List<SessionSummary> = gateway.getSessions()

    suspend fun getHealth(): HealthStatus = gateway.getHealth()
}
