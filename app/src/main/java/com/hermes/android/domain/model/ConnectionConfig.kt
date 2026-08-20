package com.hermes.android.domain.model

import kotlinx.serialization.Serializable

/** How the app reaches a Hermes backend. */
@Serializable
enum class ConnectionMode { LOCAL, REMOTE }

/**
 * Persisted connection settings. LOCAL = Termux loopback (http://127.0.0.1:8642),
 * REMOTE = a hosted API server (URL + key).
 */
@Serializable
data class ConnectionConfig(
    val mode: ConnectionMode,
    val baseUrl: String,
    val apiKey: String = "",
    val activeAgentId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val isConfigured: Boolean get() = baseUrl.isNotBlank()

    companion object {
        val DEFAULT_LOCAL = ConnectionConfig(
            mode = ConnectionMode.LOCAL,
            baseUrl = "http://127.0.0.1:8642"
        )
    }
}
