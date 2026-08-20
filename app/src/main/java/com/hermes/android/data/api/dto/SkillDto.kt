package com.hermes.android.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SkillDto(
    val id: String,
    val name: String,
    val description: String = "",
    val version: String = "",
    val category: String = "",
    val author: String = "",
    val source: String = "",
    val installed: Boolean = false
) {
    fun toDomain() = com.hermes.android.domain.model.Skill(
        id = id,
        name = name,
        description = description,
        version = version,
        category = category,
        author = author,
        source = source,
        installed = installed
    )
}
