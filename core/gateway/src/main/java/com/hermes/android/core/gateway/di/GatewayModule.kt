package com.hermes.android.core.gateway.di

import com.hermes.android.core.gateway.GatewayConfigProvider
import com.hermes.android.core.gateway.HermesGatewayClient
import com.hermes.android.core.gateway.InMemoryPendingApprovalsStore
import com.hermes.android.core.gateway.KtorHermesGatewayClient
import com.hermes.android.core.gateway.PendingApprovalsStore
import org.koin.dsl.module

/**
 * Wires the gateway client + shared approval store.
 *
 * The active [GatewayConfigProvider] is contributed by the data layer
 * ([com.hermes.android.core.data.dataModule]) and reads the live connection
 * profile, so the client always uses the current host / API key. The caller
 * (app shell) must still install [com.hermes.android.core.data.dataModule]
 * (which includes this module) so the provider resolves.
 */
fun gatewayModule() = module {
    single<PendingApprovalsStore> { InMemoryPendingApprovalsStore() }
    single<HermesGatewayClient> { KtorHermesGatewayClient(get<GatewayConfigProvider>()) }
}
