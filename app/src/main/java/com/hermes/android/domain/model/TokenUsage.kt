package com.hermes.android.domain.model

/** Token + cost accounting for a completed stream. */
data class TokenUsage(
    val tokensIn: Int = 0,
    val tokensOut: Int = 0,
    val cost: Double = 0.0
)
