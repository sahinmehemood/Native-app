package com.hermes.android.domain.model

/** Role of a chat participant. */
enum class MessageRole { USER, ASSISTANT, SYSTEM, TOOL }

/** Lifecycle status of a message. */
enum class MessageStatus { PENDING, STREAMING, DONE, ERROR, CANCELLED }

/**
 * A single chat message. The chat screen renders these in a [androidx.compose.foundation.lazy.LazyColumn].
 */
data class Message(
    val id: String,
    val sessionId: String,
    val role: MessageRole,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.DONE,
    val model: String? = null,
    val tokensIn: Int = 0,
    val tokensOut: Int = 0,
    val toolCalls: List<ToolCall> = emptyList()
)
