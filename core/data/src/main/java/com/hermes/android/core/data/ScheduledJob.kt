package com.hermes.android.core.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A user-defined, client-side scheduled job (a "Hermes automation").
 *
 * There is **no** server-side cron/automation endpoint in the Hermes mobile
 * contract (verified against `docs/HERMES-MOBILE-API.md` and the gateway
 * source). These jobs are therefore an honest, device-local planner: each one
 * carries the prompt that is dispatched to a Hermes session when the user taps
 * **Run** (or when a future background scheduler fires). Nothing here fabricates
 * server state.
 *
 * @param id Stable unique id.
 * @param name Human label shown in the list.
 * @param schedule Free-text schedule description (e.g. "Daily 09:00", "Mon/Fri").
 * @param prompt The instruction dispatched to Hermes on run.
 * @param createdAt ISO-8601 creation timestamp.
 * @param lastRunAt ISO-8601 timestamp of the last successful dispatch, if any.
 */
@Serializable
data class ScheduledJob(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("schedule") val schedule: String,
    @SerialName("prompt") val prompt: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("last_run_at") val lastRunAt: String? = null,
)

/**
 * Persistence contract for scheduled jobs, implemented by
 * [ScheduledJobRepository] (DataStore-backed). Exists as an interface so the
 * Automations feature can be unit-tested with an in-memory fake.
 */
interface ScheduledJobStore {
    val jobs: StateFlow<List<ScheduledJob>>
    suspend fun addJob(job: ScheduledJob)
    suspend fun removeJob(id: String)
    suspend fun markRun(id: String, at: String)
}
