package com.hermes.android.feature.activity

import com.hermes.android.core.data.SessionRepository
import com.hermes.android.core.gateway.HermesGatewayClient
import com.hermes.android.core.gateway.model.ApprovalResult
import com.hermes.android.core.gateway.model.Capabilities
import com.hermes.android.core.gateway.model.ChatRequest
import com.hermes.android.core.gateway.model.HealthStatus
import com.hermes.android.core.gateway.model.Message
import com.hermes.android.core.gateway.model.SessionSummary
import com.hermes.android.core.gateway.model.StreamEvent
import com.hermes.android.feature.activity.viewmodel.ActivityStatus
import com.hermes.android.feature.activity.viewmodel.ActivityViewModel
import com.hermes.android.feature.activity.viewmodel.ItemMessagesStatus
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

private class FakeGateway(
    var sessions: List<SessionSummary> = emptyList(),
    var messages: List<Message> = emptyList(),
) : HermesGatewayClient {
    override suspend fun getHealth(): HealthStatus = HealthStatus(status = "ok")
    override suspend fun getSessions(): List<SessionSummary> = sessions
    override suspend fun createSession(): SessionSummary = SessionSummary(id = "x")
    override suspend fun getSessionMessages(sessionId: String): List<Message> = messages
    override suspend fun deleteSession(sessionId: String) = Unit
    override suspend fun getCapabilities(): Capabilities = Capabilities()
    override fun postChat(sessionId: String, request: ChatRequest) = emptyFlow<StreamEvent>()
    override fun getRunEvents(runId: String) = emptyFlow<StreamEvent>()
    override suspend fun postApproval(runId: String, decision: String, scope: String?) = ApprovalResult()
    override suspend fun stopRun(runId: String) = Unit
}

class ActivityViewModelTest {
    @Test
    fun load_sorts_recent_first_and_marks_ready() = runTest {
        val gw = FakeGateway(
            listOf(
                SessionSummary(id = "old", updatedAt = "2024-01-01T00:00:00Z"),
                SessionSummary(id = "new", updatedAt = "2024-06-01T00:00:00Z"),
            ),
        )
        val vm = ActivityViewModel(SessionRepository(gw))
        advanceUntilIdle()
        assertEquals(ActivityStatus.Ready, vm.state.value.status)
        assertEquals("new", vm.state.value.items.first().session.id)
    }

    @Test
    fun expand_loads_messages_for_row() = runTest {
        val gw = FakeGateway(
            listOf(SessionSummary(id = "s1", updatedAt = "2024-06-01T00:00:00Z")),
            messages = listOf(Message(role = "user", content = "hi")),
        )
        val vm = ActivityViewModel(SessionRepository(gw))
        advanceUntilIdle()
        vm.toggleExpand("s1")
        advanceUntilIdle()
        val item = vm.state.value.items.first()
        assertEquals(true, item.expanded)
        assertEquals(ItemMessagesStatus.Ready, item.messagesStatus)
        assertEquals(1, item.messages.size)
    }
}
