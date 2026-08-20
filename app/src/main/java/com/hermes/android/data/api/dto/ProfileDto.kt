package com.hermes.android.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    val name: String,
    val description: String = "",
    val avatar: String? = null,
    val model: String? = null,
    val provider: String? = null
) {
    fun toDomain() = com.hermes.android.domain.model.Agent(
        id = id,
        name = name,
        description = description,
        avatar = avatar,
        model = model,
        provider = provider
    )
}
