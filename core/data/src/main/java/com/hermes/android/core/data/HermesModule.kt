package com.hermes.android.core.data

import com.hermes.android.core.gateway.GatewayConfig
import com.hermes.android.core.gateway.di.gatewayModule
import org.koin.android.ext.android.androidContext
import org.koin.dsl.module

/**
 * Wires the data layer: connection profile (DataStore), session repository, the
 * derived [GatewayConfig] the gateway client needs, and the shared
 * [ConnectionState] signal.
 *
 * Including [gatewayModule] here means any app that installs [dataModule]
 * automatically gets a fully-configured [com.hermes.android.core.gateway.HermesGatewayClient].
 */
fun dataModule() = module {
    includes(gatewayModule())

    single { ConnectionRepository(androidContext()) }
    single<GatewayConfig> { get<ConnectionRepository>().config.value }
    single { SessionRepository(get()) }
    single { ConnectionState(get()) }
}
