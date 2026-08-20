package com.hermes.android.domain.model

/** Status of an in-flight tool invocation surfaced in the chat. */
enum class ToolCallStatus { PENDING, RUNNING, DONE, ERROR }

/**
 * A tool call emitted by the agent during a stream. Rendered by HermesToolCall.
 */
data class ToolCall(
    val id: String,
    val name: String,
    val args: Map<String, String> = emptyMap(),
    val status: ToolCallStatus = ToolCallStatus.PENDING,
    val progress: Float = 0f,
    val result: String? = null,
    val error: String? = null
)
