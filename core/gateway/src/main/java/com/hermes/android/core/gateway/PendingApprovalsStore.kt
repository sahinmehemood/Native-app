package com.hermes.android.core.gateway

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Shared, process-local count of runs awaiting user approval.
 *
 * Home observes [count] for its pending-approvals badge; Chat adds/removes run
 * ids as `awaiting_approval` frames arrive and are resolved. Both features share
 * one instance via Koin so the badge stays in sync with active chats.
 */
interface PendingApprovalsStore {
    val count: StateFlow<Int>
    fun add(runId: String)
    fun remove(runId: String)
}

class InMemoryPendingApprovalsStore : PendingApprovalsStore {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
    private val ids = MutableStateFlow<Set<String>>(emptySet())

    override val count: StateFlow<Int> =
        ids.map { it.size }.stateIn(scope, SharingStarted.Eagerly, 0)

    override fun add(runId: String) {
        ids.value = ids.value + runId
    }

    override fun remove(runId: String) {
        ids.value = ids.value - runId
    }
}
