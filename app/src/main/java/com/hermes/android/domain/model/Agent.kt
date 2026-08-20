package com.hermes.android.domain.model

/**
 * An agent (Hermes "profile"). Maps to GET /api/v1/profiles.
 */
data class Agent(
    val id: String,
    val name: String,
    val description: String = "",
    val avatar: String? = null,
    val model: String? = null,
    val provider: String? = null,
    val isActive: Boolean = false
)
