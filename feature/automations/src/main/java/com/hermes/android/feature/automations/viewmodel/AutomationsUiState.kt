package com.hermes.android.feature.automations.viewmodel

import com.hermes.android.core.data.ScheduledJob

enum class AutomationsStatus { Loading, Ready, Empty }

/** Per-job run lifecycle for the "Run" action. */
enum class JobRunState { Idle, Running, Success, Error }

data class AutomationsUiState(
    val status: AutomationsStatus = AutomationsStatus.Loading,
    val jobs: List<ScheduledJob> = emptyList(),
    val showAdd: Boolean = false,
    /** Job currently being edited (null when adding a brand-new job). */
    val editing: ScheduledJob? = null,
    val runStates: Map<String, JobRunState> = emptyMap(),
    val runError: String? = null,
    /** Session id of the most recently dispatched run, for "Open" affordance. */
    val lastRunSessionId: String? = null,
)

/** Produce an ISO-8601-ish timestamp without depending on java.time (API-gated). */
fun nowTimestamp(): String {
    val fmt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
    return fmt.format(java.util.Date())
}
