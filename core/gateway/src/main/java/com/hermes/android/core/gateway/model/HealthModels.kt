package com.hermes.android.core.gateway.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HealthStatus(
    @SerialName("status") val status: String = "unknown",
    @SerialName("version") val version: String? = null,
    @SerialName("uptime_seconds") val uptimeSeconds: Double? = null,
)

@Serializable
data class ApprovalDecision(
    @SerialName("decision") val decision: String, // once|session|always|deny
    @SerialName("scope") val scope: String? = null,
)

@Serializable
data class ApprovalResult(
    @SerialName("status") val status: String = "ok",
    @SerialName("message") val message: String? = null,
)
