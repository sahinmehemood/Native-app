package com.hermes.android.domain.model

/**
 * Events emitted by the streaming chat endpoint (SSE). Mirrors the Hermes Desktop
 * SSE contract documented in docs/API_INTEGRATION.md §4.3.
 */
sealed interface ChatEvent {
    data class MessageStart(val sessionId: String, val title: String? = null) : ChatEvent
    data class MessageDelta(val delta: String, val content: String = delta) : ChatEvent
    data class MessageEnd(
        val sessionId: String,
        val tokensIn: Int = 0,
        val tokensOut: Int = 0,
        val cost: Double = 0.0
    ) : ChatEvent
    data class ToolStart(val name: String, val args: Map<String, String> = emptyMap()) : ChatEvent
    data class ToolProgress(val name: String, val progress: Float, val message: String? = null) : ChatEvent
    data class ToolEnd(val name: String, val result: String? = null, val error: String? = null) : ChatEvent
    data class Thinking(val content: String) : ChatEvent
    data class Error(val message: String, val code: Int? = null) : ChatEvent
    data class Done(val sessionId: String) : ChatEvent
}
