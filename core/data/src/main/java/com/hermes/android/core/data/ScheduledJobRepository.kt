package com.hermes.android.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hermes.android.core.gateway.HermesJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Device-local store of [ScheduledJob]s (the "Automations" planner).
 *
 * Backed by its own DataStore (app-private, process-safe). Jobs are persisted
 * as a JSON array; the live list is surfaced as [jobs] so the Automations screen
 * updates instantly on add/remove. The API key is never involved here.
 *
 * Per AGENTS.md the key is never in the APK and the store is always app-private.
 */
class ScheduledJobRepository(private val context: Context) : ScheduledJobStore {
    private val dataStore: DataStore<Preferences> = context.automationsDataStore

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _jobs = MutableStateFlow<List<ScheduledJob>>(emptyList())
    override val jobs: StateFlow<List<ScheduledJob>> = _jobs.asStateFlow()

    init {
        scope.launch {
            dataStore.data
                .map { prefs -> decode(prefs[KEY_JOBS]) }
                .collect { _jobs.value = it }
        }
    }

    override suspend fun addJob(job: ScheduledJob) = persist(_jobs.value + job)

    override suspend fun removeJob(id: String) = persist(_jobs.value.filter { it.id != id })

    override suspend fun markRun(id: String, at: String) =
        persist(_jobs.value.map { if (it.id == id) it.copy(lastRunAt = at) else it })

    private suspend fun persist(list: List<ScheduledJob>) {
        dataStore.edit { it[KEY_JOBS] = HermesJson.encodeToString(list) }
        _jobs.value = list
    }

    private fun decode(raw: String?): List<ScheduledJob> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching { HermesJson.decodeFromString<List<ScheduledJob>>(raw) }.getOrDefault(emptyList())
    }

    companion object {
        private val KEY_JOBS = stringPreferencesKey("jobs")
    }
}

private val Context.automationsDataStore by preferencesDataStore(name = "hermes_automations")
