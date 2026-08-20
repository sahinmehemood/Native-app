package com.hermes.android.domain.model

/** A Hermes skill. Maps to GET /api/v1/skills. */
data class Skill(
    val id: String,
    val name: String,
    val description: String = "",
    val version: String = "",
    val category: String = "",
    val author: String = "",
    val source: String = "",
    val installed: Boolean = false
)
