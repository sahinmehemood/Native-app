package com.hermes.android.feature.nous

import com.hermes.android.core.data.SessionRepository
import com.hermes.android.core.gateway.HermesGatewayClient
import com.hermes.android.core.gateway.model.ApprovalResult
import com.hermes.android.core.gateway.model.Capabilities
import com.hermes.android.core.gateway.model.ChatRequest
import com.hermes.android.core.gateway.model.HealthStatus
import com.hermes.android.core.gateway.model.Message
import com.hermes.android.core.gateway.model.SessionSummary
import com.hermes.android.core.gateway.model.StreamEvent
import com.hermes.android.feature.nous.viewmodel.NousCaptureStatus
import com.hermes.android.feature.nous.viewmodel.NousViewModel
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class FakeGateway : HermesGatewayClient {
    var createCalls = 0
    var lastPostedSessionId: String? = null
    var lastPostedMessage: String? = null
    override suspend fun getHealth(): HealthStatus = HealthStatus(status = "ok")
    override suspend fun getSessions(): List<SessionSummary> = listOf(SessionSummary(id = "s1", title = "Existing"))
    override suspend fun createSession(): SessionSummary { createCalls++; return SessionSummary(id = "new-$createCalls", title = "New") }
    override suspend fun getSessionMessages(sessionId: String): List<Message> = emptyList()
    override suspend fun deleteSession(sessionId: String) = Unit
    override suspend fun getCapabilities(): Capabilities = Capabilities()
    override fun postChat(sessionId: String, request: ChatRequest): kotlinx.coroutines.flow.Flow<StreamEvent> {
        lastPostedSessionId = sessionId
        lastPostedMessage = request.message
        return emptyFlow()
    }
    override fun getRunEvents(runId: String) = emptyFlow<StreamEvent>()
    override suspend fun postApproval(runId: String, decision: String, scope: String?) = ApprovalResult()
    override suspend fun stopRun(runId: String) = Unit
}

class NousViewModelTest {
    @Test
    fun capture_creates_session_and_posts_when_target_is_new() = runTest {
        val gw = FakeGateway()
        val vm = NousViewModel(SessionRepository(gw))
        advanceUntilIdle()

        vm.onCaptureTextChange("remember the meeting")
        vm.onTargetChange(null)
        vm.capture()
        advanceUntilIdle()

        assertEquals(1, gw.createCalls)
        assertEquals("remember the meeting", gw.lastPostedMessage)
        assertEquals(NousCaptureStatus.Success, vm.state.value.captureStatus)
        assertEquals("new-1", vm.state.value.lastCapturedSessionId)
    }

    @Test
    fun capture_posts_to_existing_session_without_creating() = runTest {
        val gw = FakeGateway()
        val vm = NousViewModel(SessionRepository(gw))
        advanceUntilIdle()

        vm.onCaptureTextChange("a link")
        vm.onTargetChange("s1")
        vm.capture()
        advanceUntilIdle()

        assertEquals(0, gw.createCalls)
        assertEquals("s1", gw.lastPostedSessionId)
        assertEquals(NousCaptureStatus.Success, vm.state.value.captureStatus)
    }

    @Test
    fun capture_ignores_blank_text() = runTest {
        val gw = FakeGateway()
        val vm = NousViewModel(SessionRepository(gw))
        advanceUntilIdle()
        vm.onCaptureTextChange("   ")
        vm.capture()
        advanceUntilIdle()
        assertEquals(NousCaptureStatus.Idle, vm.state.value.captureStatus)
        assertEquals(0, gw.createCalls)
    }
}
