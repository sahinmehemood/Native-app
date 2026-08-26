package com.hermes.android.core.gateway

import com.hermes.android.core.gateway.model.SessionSummary

/**
 * Supplies the active [GatewayConfig] to the transport layer.
 *
 * The app shell provides a live implementation backed by [com.hermes.android.core.data.ConnectionRepository]
 * so that a Settings change (host / API key) takes effect on the very next
 * request without tearing down the Koin graph. Tests and one-off probes (e.g.
 * "Test connection" with not-yet-saved credentials) use [FixedGatewayConfigProvider]
 * or [gatewayClientFor] to build a client pinned to an explicit config.
 */
interface GatewayConfigProvider {
    fun current(): GatewayConfig
}

/** Static config holder — used for tests and explicit credential probes. */
class FixedGatewayConfigProvider(private val config: GatewayConfig) : GatewayConfigProvider {
    override fun current(): GatewayConfig = config
}

/**
 * Build a [HermesGatewayClient] pinned to [config]. Handy for connection probes
 * that must use credentials the user typed but has not yet saved.
 */
fun gatewayClientFor(config: GatewayConfig): HermesGatewayClient =
    KtorHermesGatewayClient(FixedGatewayConfigProvider(config))
