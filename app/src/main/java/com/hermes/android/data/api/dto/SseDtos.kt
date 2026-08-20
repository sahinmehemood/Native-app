package com.hermes.android.data.api.dto

import com.hermes.android.domain.model.ChatEvent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class SseMessageStart(
    @SerialName("session_id") val sessionId: String = "",
    val title: String = ""
) {
    fun toEvent() = ChatEvent.MessageStart(sessionId, title.ifBlank { null })
}

@Serializable
data class SseMessageDelta(
    val delta: String = "",
    val content: String = "",
    @SerialName("session_id") val sessionId: String = ""
) {
    fun toEvent() = ChatEvent.MessageDelta(delta, content.ifBlank { delta })
}

@Serializable
data class SseMessageEnd(
    @SerialName("session_id") val sessionId: String = "",
    @SerialName("tokens_in") val tokensIn: Int = 0,
    @SerialName("tokens_out") val tokensOut: Int = 0,
    val cost: Double = 0.0
) {
    fun toEvent() = ChatEvent.MessageEnd(sessionId, tokensIn, tokensOut, cost)
}

@Serializable
data class SseTool(
    val name: String = "",
    val args: Map<String, JsonElement> = emptyMap(),
    val progress: Float = 0f,
    val message: String = "",
    val result: String = "",
    val error: String = ""
) {
    fun toStart() = ChatEvent.ToolStart(name, args.mapValues { it.value.jsonPrimitive.contentOrNull ?: it.value.toString() })
    fun toProgress() = ChatEvent.ToolProgress(name, progress, message.ifBlank { null })
    fun toEnd() = ChatEvent.ToolEnd(name, result.ifBlank { null }, error.ifBlank { null })
}

@Serializable
data class SseThinking(val content: String = "") {
    fun toEvent() = ChatEvent.Thinking(content)
}

@Serializable
data class SseError(val message: String = "", val code: Int = 0) {
    fun toEvent() = ChatEvent.Error(message, code.takeIf { it != 0 })
}

@Serializable
data class SseDone(@SerialName("session_id") val sessionId: String = "") {
    fun toEvent() = ChatEvent.Done(sessionId)
}
