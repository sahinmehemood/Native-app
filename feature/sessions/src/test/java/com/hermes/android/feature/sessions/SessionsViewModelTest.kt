package com.hermes.android.feature.sessions

import com.hermes.android.core.data.SessionRepository
import com.hermes.android.core.gateway.HermesGatewayClient
import com.hermes.android.core.gateway.model.ApprovalResult
import com.hermes.android.core.gateway.model.Capabilities
import com.hermes.android.core.gateway.model.ChatRequest
import com.hermes.android.core.gateway.model.HealthStatus
import com.hermes.android.core.gateway.model.Message
import com.hermes.android.core.gateway.model.SessionSummary
import com.hermes.android.core.gateway.model.StreamEvent
import com.hermes.android.feature.sessions.viewmodel.SessionsStatus
import com.hermes.android.feature.sessions.viewmodel.SessionsViewModel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/** Minimal gateway double for the sessions ViewModel tests. */
private class FakeGateway(
    var sessions: List<SessionSummary> = emptyList(),
) : HermesGatewayClient {
    var deletedId: String? = null
    override suspend fun getHealth(): HealthStatus = HealthStatus(status = "ok")
    override suspend fun getSessions(): List<SessionSummary> = sessions
    override suspend fun createSession(): SessionSummary = SessionSummary(id = "x")
    override suspend fun getSessionMessages(sessionId: String): List<Message> = emptyList()
    override suspend fun deleteSession(sessionId: String) { deletedId = sessionId }
    override suspend fun getCapabilities(): Capabilities = Capabilities()
    override fun postChat(sessionId: String, request: ChatRequest) = emptyFlow<StreamEvent>()
    override fun getRunEvents(runId: String) = emptyFlow<StreamEvent>()
    override suspend fun postApproval(runId: String, decision: String, scope: String?) = ApprovalResult()
    override suspend fun stopRun(runId: String) = Unit
}

class SessionsViewModelTest {
    @Test
    fun load_marks_ready_with_sessions() = runTest {
        val gw = FakeGateway(listOf(SessionSummary(id = "1", title = "A", messageCount = 3)))
        val vm = SessionsViewModel(SessionRepository(gw))
        advanceUntilIdle()
        assertEquals(SessionsStatus.Ready, vm.state.value.status)
        assertEquals(1, vm.state.value.sessions.size)
    }

    @Test
    fun load_marks_empty_when_no_sessions() = runTest {
        val gw = FakeGateway(emptyList())
        val vm = SessionsViewModel(SessionRepository(gw))
        advanceUntilIdle()
        assertEquals(SessionsStatus.Empty, vm.state.value.status)
    }

    @Test
    fun delete_invokes_gateway_and_removes_row() = runTest {
        val gw = FakeGateway(listOf(SessionSummary(id = "1"), SessionSummary(id = "2")))
        val vm = SessionsViewModel(SessionRepository(gw))
        advanceUntilIdle()
        vm.deleteSession("1")
        advanceUntilIdle()
        assertEquals("1", gw.deletedId)
        assertEquals(1, vm.state.value.sessions.size)
        assertEquals("2", vm.state.value.sessions.first().id)
    }
}
