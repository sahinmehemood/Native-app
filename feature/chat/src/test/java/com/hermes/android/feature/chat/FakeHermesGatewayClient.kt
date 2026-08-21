package com.hermes.android.feature.chat

import com.hermes.android.core.gateway.HermesGatewayClient
import com.hermes.android.core.gateway.model.ApprovalResult
import com.hermes.android.core.gateway.model.ChatRequest
import com.hermes.android.core.gateway.model.HealthStatus
import com.hermes.android.core.gateway.model.SessionSummary
import com.hermes.android.core.gateway.model.StreamEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Test double for [HermesGatewayClient]. Records call counts so the idempotency
 * and approval-resolution tests can assert exactly how many times the network
 * layer was touched.
 */
class FakeHermesGatewayClient(
    private val chatFlow: Flow<StreamEvent> = emptyFlow(),
) : HermesGatewayClient {
    var postChatCalls = 0
        private set
    var lastChatSessionId: String? = null
        private set
    var postApprovalCalls = 0
        private set
    var lastApprovalRunId: String? = null
        private set
    var lastApprovalDecision: String? = null
        private set
    var lastApprovalScope: String? = null
        private set

    override suspend fun getHealth(): HealthStatus = HealthStatus(status = "ok")
    override suspend fun getSessions(): List<SessionSummary> = emptyList()
    override fun postChat(sessionId: String, request: ChatRequest): Flow<StreamEvent> {
        postChatCalls++
        lastChatSessionId = sessionId
        return chatFlow
    }
    override fun getRunEvents(runId: String): Flow<StreamEvent> = emptyFlow()
    override suspend fun postApproval(runId: String, decision: String, scope: String?): ApprovalResult {
        postApprovalCalls++
        lastApprovalRunId = runId
        lastApprovalDecision = decision
        lastApprovalScope = scope
        return ApprovalResult(status = "ok")
    }
    override suspend fun stopRun(runId: String) = Unit
}
