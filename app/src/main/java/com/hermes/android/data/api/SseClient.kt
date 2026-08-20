package com.hermes.android.data.api

import com.hermes.android.data.api.dto.SseDone
import com.hermes.android.data.api.dto.SseError
import com.hermes.android.data.api.dto.SseMessageDelta
import com.hermes.android.data.api.dto.SseMessageEnd
import com.hermes.android.data.api.dto.SseMessageStart
import com.hermes.android.data.api.dto.SseThinking
import com.hermes.android.data.api.dto.SseTool
import com.hermes.android.domain.model.ChatEvent
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

/**
 * Parses a Server-Sent Events stream from the Hermes /chat endpoint into a
 * [ChatEvent] flow. Tolerant: unknown events / malformed JSON are skipped.
 */
object SseClient {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    fun parse(response: HttpResponse): Flow<ChatEvent> = flow {
        val channel: ByteReadChannel = response.bodyAsChannel()
        val builder = StringBuilder()
        while (!channel.isClosedForRead) {
            val line = channel.readUTF8Line() ?: break
            if (line.isEmpty()) {
                parseEvent(builder.toString())?.let { emit(it) }
                builder.clear()
            } else {
                builder.append(line).append('\n')
            }
        }
        if (builder.isNotEmpty()) parseEvent(builder.toString())?.let { emit(it) }
    }

    private fun parseEvent(block: String): ChatEvent? {
        var type = ""
        val dataLines = mutableListOf<String>()
        block.lines().forEach { raw ->
            val line = raw.trimEnd()
            when {
                line.startsWith("event:") -> type = line.removePrefix("event:").trim()
                line.startsWith("data:") -> dataLines.add(line.removePrefix("data:").trim())
                else -> Unit
            }
        }
        val data = dataLines.joinToString("\n").ifBlank { return null }
        return try {
            when (type) {
                "message_start" -> json.decodeFromString<SseMessageStart>(data).toEvent()
                "message" -> json.decodeFromString<SseMessageDelta>(data).toEvent()
                "message_end" -> json.decodeFromString<SseMessageEnd>(data).toEvent()
                "tool_start" -> json.decodeFromString<SseTool>(data).toStart()
                "tool_progress" -> json.decodeFromString<SseTool>(data).toProgress()
                "tool_end" -> json.decodeFromString<SseTool>(data).toEnd()
                "thinking" -> json.decodeFromString<SseThinking>(data).toEvent()
                "error" -> json.decodeFromString<SseError>(data).toEvent()
                "done" -> json.decodeFromString<SseDone>(data).toEvent()
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }
}
