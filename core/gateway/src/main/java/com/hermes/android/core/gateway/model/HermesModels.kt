package com.hermes.android.core.gateway.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Wire models for the Hermes `api_server` contract (see docs/HERMES-MOBILE-API.md).
 *
 * All models are **unknown-field tolerant**: serializers use
 * [kotlinx.serialization.json.Json] configured with `ignoreUnknownKeys = true`
 * (see [com.hermes.android.core.gateway.HermesJson]). The client MUST tolerate
 * new fields/events without breaking — it never invents behavior for unknown
 * commands.
 */

@Serializable
data class ModelInfo(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String? = null,
    @SerialName("owned_by") val ownedBy: String? = null,
)

@Serializable
data class Capabilities(
    @SerialName("version") val version: String? = null,
    @SerialName("features") val features: Map<String, JsonElement> = emptyMap(),
)

@Serializable
data class SessionSummary(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("message_count") val messageCount: Int = 0,
)

@Serializable
data class Message(
    @SerialName("id") val id: String? = null,
    @SerialName("role") val role: String,           // "user" | "assistant" | "system" | "tool"
    @SerialName("content") val content: String,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class ChatRequest(
    @SerialName("message") val message: String,
    @SerialName("model") val model: String? = null,
    @SerialName("provider") val provider: String? = null,
    @SerialName("model_options") val modelOptions: ModelOptions? = null,
    @SerialName("stream") val stream: Boolean = true,
)

@Serializable
data class ModelOptions(
    @SerialName("reasoning_effort") val reasoningEffort: String? = null,
    @SerialName("reasoning") val reasoning: Reasoning? = null,
)

@Serializable
data class Reasoning(
    @SerialName("enabled") val enabled: Boolean? = null,
    @SerialName("effort") val effort: String? = null,
)

// ── SSE frame payloads ───────────────────────────────────────────────────────

@Serializable
data class DeltaFrame(
    @SerialName("text") val text: String = "",
)

@Serializable
data class ToolFrame(
    @SerialName("tool_name") val toolName: String = "",
    @SerialName("preview") val preview: String? = null,
    @SerialName("args") val args: JsonElement? = null,
    @SerialName("index") val index: Int = 0,
    @SerialName("phase") val phase: String = "start", // "start" | "finish"
    @SerialName("ok") val ok: Boolean = true,
    @SerialName("duration") val duration: Double = 0.0,
)

@Serializable
data class ThinkingFrame(
    @SerialName("text") val text: String = "",
)

@Serializable
data class RunStatusFrame(
    @SerialName("run_id") val runId: String,
    @SerialName("status") val status: String, // queued|running|awaiting_approval|done|stopped
    @SerialName("approval") val approval: ApprovalPayload? = null,
)

@Serializable
data class ApprovalPayload(
    @SerialName("id") val id: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("detail") val detail: String? = null,
    // scope options the gateway accepts: once|session|always|deny
    @SerialName("choices") val choices: List<String> = listOf("once", "session", "deny"),
)

@Serializable
data class ErrorFrame(
    @SerialName("code") val code: String? = null,
    @SerialName("message") val message: String = "Unknown error",
)

/**
 * A single SSE event on the chat/run streams. `event` names: delta | message |
 * tool | thinking | run | error. `data` is the parsed payload for that event.
 */
@Serializable
data class StreamEvent(
    @SerialName("event") val event: String = "message",
    @SerialName("data") val data: JsonElement,
)
