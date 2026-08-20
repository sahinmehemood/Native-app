package com.hermes.android.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequestDto(
    val message: String,
    @SerialName("session_id") val sessionId: String? = null,
    val model: String? = null,
    val profile: String? = null,
    val stream: Boolean = true
)
