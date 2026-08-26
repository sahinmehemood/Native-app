package com.hermes.android.feature.automations

import com.hermes.android.core.data.SessionRepository
import com.hermes.android.core.data.ScheduledJob
import com.hermes.android.core.data.ScheduledJobStore
import com.hermes.android.core.gateway.HermesGatewayClient
import com.hermes.android.core.gateway.model.ApprovalResult
import com.hermes.android.core.gateway.model.Capabilities
import com.hermes.android.core.gateway.model.ChatRequest
import com.hermes.android.core.gateway.model.HealthStatus
import com.hermes.android.core.gateway.model.Message
import com.hermes.android.core.gateway.model.SessionSummary
import com.hermes.android.core.gateway.model.StreamEvent
import com.hermes.android.feature.automations.viewmodel.AutomationsStatus
import com.hermes.android.feature.automations.viewmodel.AutomationsViewModel
import com.hermes.android.feature.automations.viewmodel.JobRunState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class FakeJobStore : ScheduledJobStore {
    override val jobs = MutableStateFlow<List<ScheduledJob>>(emptyList())
    override suspend fun addJob(job: ScheduledJob) { jobs.value = jobs.value + job }
    override suspend fun removeJob(id: String) { jobs.value = jobs.value.filter { it.id != id } }
    override suspend fun markRun(id: String, at: String) {
        jobs.value = jobs.value.map { if (it.id == id) it.copy(lastRunAt = at) else it }
    }
}

private class FakeGateway : HermesGatewayClient {
    var created: String? = null
    var postedTo: String? = null
    var postedPrompt: String? = null
    override suspend fun getHealth(): HealthStatus = HealthStatus(status = "ok")
    override suspend fun getSessions(): List<SessionSummary> = emptyList()
    override suspend fun createSession(): SessionSummary { created = "sess-1"; return SessionSummary(id = "sess-1") }
    override suspend fun getSessionMessages(sessionId: String): List<Message> = emptyList()
    override suspend fun deleteSession(sessionId: String) = Unit
    override suspend fun getCapabilities(): Capabilities = Capabilities()
    override fun postChat(sessionId: String, request: ChatRequest): kotlinx.coroutines.flow.Flow<StreamEvent> {
        postedTo = sessionId
        postedPrompt = request.message
        return emptyFlow()
    }
    override fun getRunEvents(runId: String) = emptyFlow<StreamEvent>()
    override suspend fun postApproval(runId: String, decision: String, scope: String?) = ApprovalResult()
    override suspend fun stopRun(runId: String) = Unit
}

class AutomationsViewModelTest {
    @Test
    fun saveJob_adds_to_store_and_marks_ready() = runTest {
        val vm = AutomationsViewModel(FakeJobStore(), SessionRepository(FakeGateway()))
        advanceUntilIdle()
        assertEquals(AutomationsStatus.Empty, vm.state.value.status)

        vm.showAdd()
        vm.saveJob("Daily standup", "Daily 09:00", "Summarize my calendar")
        advanceUntilIdle()

        assertEquals(1, vm.state.value.jobs.size)
        assertEquals("Daily standup", vm.state.value.jobs.first().name)
        assertEquals(AutomationsStatus.Ready, vm.state.value.status)
    }

    @Test
    fun saveJob_ignores_blank_name_or_prompt() = runTest {
        val store = FakeJobStore()
        val vm = AutomationsViewModel(store, SessionRepository(FakeGateway()))
        advanceUntilIdle()
        vm.showAdd()
        vm.saveJob("", "x", "prompt")
        vm.saveJob("name", "x", "   ")
        advanceUntilIdle()
        assertTrue(store.jobs.value.isEmpty())
    }

    @Test
    fun runJob_dispatches_to_gateway() = runTest {
        val store = FakeJobStore()
        val gw = FakeGateway()
        val vm = AutomationsViewModel(store, SessionRepository(gw))
        advanceUntilIdle()
        vm.showAdd()
        vm.saveJob("Digest", "Daily", "Send me a digest")
        advanceUntilIdle()
        val job = vm.state.value.jobs.first()

        var opened: String? = null
        vm.runJob(job) { opened = it }
        advanceUntilIdle()

        assertEquals("sess-1", gw.postedTo)
        assertEquals("Send me a digest", gw.postedPrompt)
        assertEquals(JobRunState.Success, vm.state.value.runStates[job.id])
        assertEquals("sess-1", opened)
        assertEquals("sess-1", vm.state.value.lastRunSessionId)
        assertEquals(true, store.jobs.value.first().lastRunAt != null)
    }
}
