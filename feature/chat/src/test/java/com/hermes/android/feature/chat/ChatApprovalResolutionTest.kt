package com.hermes.android.feature.chat

import com.hermes.android.core.gateway.InMemoryPendingApprovalsStore
import com.hermes.android.core.gateway.model.StreamEvent
import com.hermes.android.core.gateway.parseStreamEvent
import com.hermes.android.feature.chat.domain.InMemoryDraftRepository
import com.hermes.android.feature.chat.viewmodel.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatApprovalResolutionTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `awaiting approval renders card and resolves via postApproval`() = runTest {
        val chatFlow = flow<StreamEvent> {
            emit(
                parseStreamEvent(
                    "run",
                    """{"run_id":"r1","status":"awaiting_approval","approval":{"title":"Run cmd","choices":["once","session","deny"]}}""",
                ),
            )
        }
        val fake = FakeHermesGatewayClient(chatFlow = chatFlow)
        val store = InMemoryPendingApprovalsStore()
        val vm = ChatViewModel(
            gateway = fake,
            pendingApprovals = store,
            draftRepository = InMemoryDraftRepository(),
            sessionId = "s1",
        )
        vm.send()
        advanceUntilIdle()

        assertEquals("r1", vm.state.value.pendingApproval?.runId)
        assertEquals(1, store.count.value)

        vm.onApproval("once")
        advanceUntilIdle()

        assertEquals(1, fake.postApprovalCalls)
        assertEquals("r1", fake.lastApprovalRunId)
        assertEquals("once", fake.lastApprovalDecision)
        assertNull(vm.state.value.pendingApproval)
        assertEquals(0, store.count.value)
    }
}
