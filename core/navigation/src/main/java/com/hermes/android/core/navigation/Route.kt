package com.hermes.android.core.navigation

/**
 * Sealed navigation contract for the app.
 *
 * Every destination the app can reach is declared here so feature modules never
 * hard-code route strings. Home and Chat are the core flows; the remaining
 * entries are placeholders wired to "coming soon" screens so the graph resolves
 * and is extensible without touching the app shell.
 *
 * Convention: parameterized routes expose a `createRoute(...)` helper that
 * builds the concrete path, and an `ARG` constant for argument extraction.
 */
sealed class Route(val route: String) {
    data object Home : Route("home")

    data object Settings : Route("settings")

    data class Chat(val sessionId: String) : Route("chat/{sessionId}") {
        companion object {
            const val ARG = "sessionId"
            fun createRoute(sessionId: String): String = "chat/$sessionId"
        }
    }

    data object Activity : Route("activity")
    data object Sessions : Route("sessions")
    data object Nous : Route("nous")
    data object Automations : Route("automations")
}
