package com.hermes.android.feature.automations.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.core.data.SessionRepository
import com.hermes.android.core.data.ScheduledJob
import com.hermes.android.core.data.ScheduledJobStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import java.util.UUID

/**
 * Client-side automation planner.
 *
 * There is no server-side cron/automation endpoint in the Hermes mobile
 * contract (verified against `docs/HERMES-MOBILE-API.md` and the gateway
 * source), so jobs live in a local DataStore and are an honest device planner.
 * "Run" creates a real Hermes session and dispatches the job's prompt through
 * the verified `POST /api/sessions/{id}/chat` endpoint — no fabricated server
 * state. A future background scheduler can call [runJob] the same way.
 */
class AutomationsViewModel(
    private val jobRepository: ScheduledJobStore,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AutomationsUiState())
    val state: StateFlow<AutomationsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            jobRepository.jobs.collect { jobs ->
                _state.update {
                    it.copy(
                        jobs = jobs,
                        status = if (jobs.isEmpty()) AutomationsStatus.Empty else AutomationsStatus.Ready,
                    )
                }
            }
        }
    }

    fun showAdd() = _state.update { it.copy(showAdd = true, editing = null, runError = null) }

    fun editJob(job: ScheduledJob) = _state.update { it.copy(showAdd = true, editing = job, runError = null) }

    fun dismissEditor() = _state.update { it.copy(showAdd = false, editing = null) }

    /**
     * Persist a job. When [existing] is provided we update it in place
     * (preserving its id + createdAt); otherwise a fresh job is created.
     */
    fun saveJob(name: String, schedule: String, prompt: String, existing: ScheduledJob? = null) {
        val trimmedName = name.trim()
        val trimmedPrompt = prompt.trim()
        if (trimmedName.isBlank() || trimmedPrompt.isBlank()) return
        val job = existing?.copy(name = trimmedName, schedule = schedule.trim(), prompt = trimmedPrompt)
            ?: ScheduledJob(
                id = UUID.randomUUID().toString(),
                name = trimmedName,
                schedule = schedule.trim(),
                prompt = trimmedPrompt,
                createdAt = nowTimestamp(),
            )
        viewModelScope.launch { jobRepository.addJob(job) }
        _state.update { it.copy(showAdd = false, editing = null) }
    }

    fun deleteJob(job: ScheduledJob) {
        viewModelScope.launch { jobRepository.removeJob(job.id) }
    }

    /**
     * Dispatch a job: create a Hermes session and post its prompt, then record
     * the run time. On success the created session id is surfaced so the UI can
     * open it.
     */
    fun runJob(job: ScheduledJob, onDispatched: (String) -> Unit = {}) {
        if (_state.value.runStates[job.id] == JobRunState.Running) return
        _state.update { it.copy(runStates = it.runStates + (job.id to JobRunState.Running), runError = null) }
        viewModelScope.launch {
            runCatching {
                val session = sessionRepository.createSession()
                sessionRepository.postChatMessage(session.id, job.prompt)
                jobRepository.markRun(job.id, nowTimestamp())
                session.id
            }.fold(
                onSuccess = { sessionId ->
                    _state.update {
                        it.copy(
                            runStates = it.runStates + (job.id to JobRunState.Success),
                            lastRunSessionId = sessionId,
                        )
                    }
                    onDispatched(sessionId)
                },
                onFailure = { e ->
                    _state.update {
                        it.copy(
                            runStates = it.runStates + (job.id to JobRunState.Error),
                            runError = e.message ?: "Failed to run automation",
                        )
                    }
                },
            )
        }
    }

    fun clearRunError() = _state.update { it.copy(runError = null) }
}
