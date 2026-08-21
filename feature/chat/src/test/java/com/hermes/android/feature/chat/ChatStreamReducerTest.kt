package com.hermes.android.feature.chat

import com.hermes.android.core.gateway.model.StreamEvent
import com.hermes.android.core.gateway.parseStreamEvent
import com.hermes.android.feature.chat.domain.ChatStreamReducer
import com.hermes.android.feature.chat.viewmodel.ChatUiState
import org.junit.Assert.assertEquals
import org.junit.Test

class ChatStreamReducerTest {

    @Test
    fun `sse frames parse into correct ui state`() {
        val events = listOf(
            parseStreamEvent("delta", """{"text":"Hello "}"""),
            parseStreamEvent("delta", """{"text":"world"}"""),
            parseStreamEvent("tool", """{"tool_name":"search","index":0,"phase":"start"}"""),
            parseStreamEvent("tool", """{"tool_name":"search","index":0,"phase":"finish","ok":true,"duration":1.2}"""),
            parseStreamEvent("run", """{"run_id":"r1","status":"awaiting_approval","approval":{"title":"Run it","choices":["once","session","deny"]}}"""),
            parseStreamEvent("error", """{"code":"x","message":"boom"}"""),
        )
        val state = reduceAll(events)
        assertEquals("Hello world", state.messages.last().content)
        assertEquals(1, state.toolActivity.size)
        assertEquals("search", state.toolActivity.first().toolName)
        assertEquals(true, state.toolActivity.first().ok)
        assertEquals("r1", state.pendingApproval?.runId)
        assertEquals("boom", state.error)
    }

    private fun reduceAll(events: List<StreamEvent>): ChatUiState {
        val reducer = ChatStreamReducer()
        var state = ChatUiState()
        for (event in events) state = reducer.reduce(state, event)
        return state
    }
}
