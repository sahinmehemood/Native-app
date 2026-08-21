package com.hermes.android.core.gateway

import kotlinx.serialization.json.Json

/**
 * Shared JSON codec for the api_server contract.
 *
 * Unknown-field tolerant: the client must accept new fields/events emitted by the
 * gateway without breaking (docs/HERMES-MOBILE-API.md §7). It never invents
 * behavior for unknown commands.
 */
val HermesJson: Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
    encodeDefaults = true
}
