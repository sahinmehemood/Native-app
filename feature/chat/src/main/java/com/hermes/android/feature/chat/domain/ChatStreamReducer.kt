package com.hermes.android.feature.chat.domain

import com.hermes.android.core.gateway.HermesJson
import com.hermes.android.core.gateway.model.DeltaFrame
import com.hermes.android.core.gateway.model.ErrorFrame
import com.hermes.android.core.gateway.model.Message
import com.hermes.android.core.gateway.model.RunStatusFrame
import com.hermes.android.core.gateway.model.StreamEvent
import com.hermes.android.core.gateway.model.ToolFrame
import com.hermes.android.feature.chat.viewmodel.ChatMessage
import com.hermes.android.feature.chat.viewmodel.ChatUiState
import com.hermes.android.feature.chat.viewmodel.PendingApproval
import com.hermes.android.feature.chat.viewmodel.ToolActivity
import kotlinx.serialization.json.Json

/**
 * Pure, framework-free mapping from SSE [StreamEvent]s to [ChatUiState].
 *
 * This is the seam the unit tests exercise: raw frames in, UI state out.
 * Unknown events and unknown payload fields are tolerated (contract §7).
 */
class ChatStreamReducer(private val json: Json = HermesJson) {

    fun reduce(state: ChatUiState, event: StreamEvent): ChatUiState = when (event.event) {
        "delta" -> appendDelta(state, event)
        "message" -> appendMessage(state, event)
        "tool" -> updateTool(state, event)
        "run" -> updateRun(state, event)
        "error" -> state.copy(error = parseError(event), isStreaming = false)
        else -> state
    }

    private fun appendDelta(state: ChatUiState, event: StreamEvent): ChatUiState {
        val text = runCatching { json.decodeFromJsonElement(DeltaFrame.serializer(), event.data) }
            .getOrNull()?.text ?: return state
        val messages = state.messages.toMutableList()
        val last = messages.lastOrNull()
        if (last != null && last.role == "assistant" && last.isStreaming) {
            messages[messages.lastIndex] = last.copy(content = last.content + text)
        } else {
            messages.add(ChatMessage(id = "a-${messages.size}", role = "assistant", content = text, isStreaming = true))
        }
        return state.copy(messages = messages, isStreaming = true, error = null)
    }

    private fun appendMessage(state: ChatUiState, event: StreamEvent): ChatUiState {
        val msg = runCatching { json.decodeFromJsonElement(Message.serializer(), event.data) }
            .getOrNull() ?: return state
        val message = ChatMessage(
            id = msg.id ?: "m-${state.messages.size}",
            role = msg.role,
            content = msg.content,
            isStreaming = false,
        )
        return state.copy(messages = state.messages + message)
    }

    private fun updateTool(state: ChatUiState, event: StreamEvent): ChatUiState {
        val tool = runCatching { json.decodeFromJsonElement(ToolFrame.serializer(), event.data) }
            .getOrNull() ?: return state
        val entry = ToolActivity(
            index = tool.index,
            toolName = tool.toolName,
            phase = tool.phase,
            ok = tool.ok,
            duration = tool.duration,
            preview = tool.preview,
        )
        val activity = state.toolActivity.toMutableList()
        val idx = activity.indexOfFirst { it.index == tool.index && it.toolName == tool.toolName }
        if (idx >= 0) activity[idx] = entry else activity.add(entry)
        return state.copy(toolActivity = activity)
    }

    private fun updateRun(state: ChatUiState, event: StreamEvent): ChatUiState {
        val run = runCatching { json.decodeFromJsonElement(RunStatusFrame.serializer(), event.data) }
            .getOrNull() ?: return state
        return when (run.status) {
            "awaiting_approval" -> state.copy(
                pendingApproval = PendingApproval(
                    runId = run.runId,
                    title = run.approval?.title,
                    detail = run.approval?.detail,
                    choices = run.approval?.choices ?: listOf("once", "session", "deny"),
                ),
                isStreaming = false,
            )
            "done", "stopped" -> state.copy(pendingApproval = null, isStreaming = false)
            "running", "queued" -> state.copy(isStreaming = true)
            else -> state.copy(isStreaming = false)
        }
    }

    private fun parseError(event: StreamEvent): String =
        runCatching { json.decodeFromJsonElement(ErrorFrame.serializer(), event.data) }
            .fold(onSuccess = { it.message }, onFailure = { "Unknown error" })
}
