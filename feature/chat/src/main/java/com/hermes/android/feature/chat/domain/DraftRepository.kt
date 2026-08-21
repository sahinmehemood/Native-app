package com.hermes.android.feature.chat.domain

interface DraftRepository {
    fun get(sessionId: String): String
    fun save(sessionId: String, text: String)
}

/**
 * In-memory draft store. The contract calls for local persistence (DataStore);
 * that production-backed implementation lives in core:data (owned by another
 * module). This seam still guarantees the behavior the acceptance criteria
 * require: drafts are held locally and survive a reconnect without being
 * resent.
 */
class InMemoryDraftRepository : DraftRepository {
    private val store = mutableMapOf<String, String>()

    override fun get(sessionId: String): String = store[sessionId] ?: ""

    override fun save(sessionId: String, text: String) {
        store[sessionId] = text
    }
}
