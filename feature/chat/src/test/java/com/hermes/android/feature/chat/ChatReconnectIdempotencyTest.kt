package com.hermes.android.feature.chat

import com.hermes.android.core.gateway.ConnectionStatus
import com.hermes.android.core.gateway.InMemoryPendingApprovalsStore
import com.hermes.android.core.gateway.model.StreamEvent
import com.hermes.android.core.gateway.parseStreamEvent
import com.hermes.android.feature.chat.domain.InMemoryDraftRepository
import com.hermes.android.feature.chat.viewmodel.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatReconnectIdempotencyTest {
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
    fun `reconnect never resends the prompt or auto approves`() = runTest {
        val chatFlow = flow<StreamEvent> {
            emit(parseStreamEvent("delta", """{"text":"hi"}"""))
            awaitCancellation()
        }
        val fake = FakeHermesGatewayClient(chatFlow = chatFlow)
        val vm = ChatViewModel(
            gateway = fake,
            pendingApprovals = InMemoryPendingApprovalsStore(),
            draftRepository = InMemoryDraftRepository(),
            sessionId = "s1",
        )
        vm.onDraftChange("hello")
        vm.send()
        advanceUntilIdle()
        // A second send while a turn is in-flight must be ignored (idempotency guard).
        vm.send()
        advanceUntilIdle()
        // Reconnect: hard rule — no resend, no auto-approve.
        vm.onConnectionLost()
        vm.onReconnect()
        advanceUntilIdle()

        assertEquals(1, fake.postChatCalls)
        assertEquals(0, fake.postApprovalCalls)
        assertEquals(ConnectionStatus.Connected, vm.state.value.connection)
    }

    @Test
    fun `draft survives reconnect without being resent`() = runTest {
        val fake = FakeHermesGatewayClient()
        val vm = ChatViewModel(
            gateway = fake,
            pendingApprovals = InMemoryPendingApprovalsStore(),
            draftRepository = InMemoryDraftRepository(),
            sessionId = "s1",
        )
        vm.onDraftChange("persisted draft")
        vm.onConnectionLost()
        vm.onReconnect()
        advanceUntilIdle()

        assertEquals("persisted draft", vm.state.value.draft)
        assertEquals(0, fake.postChatCalls)
    }
}
