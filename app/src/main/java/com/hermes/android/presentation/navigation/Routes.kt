package com.hermes.android.presentation.navigation

/** Type-safe top-level destinations. */
sealed class Routes(val route: String) {
    data object Splash : Routes("splash")
    data object Setup : Routes("setup")
    data object Main : Routes("main")
    data object Chat : Routes("chat")
    data object Agents : Routes("agents")
    data object Skills : Routes("skills")
    data object Settings : Routes("settings")
}
