package com.hermes.android.core.gateway.di

import com.hermes.android.core.gateway.GatewayConfig
import com.hermes.android.core.gateway.HermesGatewayClient
import com.hermes.android.core.gateway.InMemoryPendingApprovalsStore
import com.hermes.android.core.gateway.KtorHermesGatewayClient
import com.hermes.android.core.gateway.PendingApprovalsStore
import org.koin.dsl.module

/**
 * Wires the gateway client + shared approval store.
 *
 * The caller (app shell) must also provide a [GatewayConfig] singleton built
 * from the Keystore/DataStore-backed connection profile (never hardcoded).
 */
fun gatewayModule() = module {
    single<PendingApprovalsStore> { InMemoryPendingApprovalsStore() }
    single<HermesGatewayClient> {
        val config = get<GatewayConfig>()
        KtorHermesGatewayClient(config.baseUrl, config.apiKey)
    }
}
