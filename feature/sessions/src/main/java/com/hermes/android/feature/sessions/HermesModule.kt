package com.hermes.android.feature.sessions

import com.hermes.android.core.navigation.HermesDestination
import com.hermes.android.core.navigation.Route
import com.hermes.android.feature.sessions.ui.SessionDetailRoute
import com.hermes.android.feature.sessions.ui.SessionsRoute
import com.hermes.android.feature.sessions.viewmodel.SessionDetailViewModel
import com.hermes.android.feature.sessions.viewmodel.SessionsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

/**
 * Sessions feature module. The [com.hermes.android.core.data.SessionRepository]
 * is provided by the data layer (via Koin).
 */
fun sessionsModule() = module {
    viewModel { SessionsViewModel(get()) }
    viewModel { params -> SessionDetailViewModel(params.get<String>(), get()) }
}

/** Nav-graph entry for the session list. */
fun sessionsDestination(
    onNavigateToDetail: (String) -> Unit,
    onNavigateUp: () -> Unit,
): HermesDestination = HermesDestination(Route.Sessions.route) {
    SessionsRoute(onNavigateUp = onNavigateUp, onSessionClick = onNavigateToDetail)
}

/** Nav-graph entry for a single session's message history. */
fun sessionDetailDestination(
    onNavigateUp: () -> Unit,
    onOpenInChat: (String) -> Unit,
): HermesDestination = HermesDestination(Route.SessionDetail.createRoute("{sessionId}")) { navController ->
    val sessionId = navController.currentBackStackEntry
        ?.arguments?.getString(Route.SessionDetail.ARG) ?: ""
    SessionDetailRoute(
        sessionId = sessionId,
        onNavigateUp = onNavigateUp,
        onOpenInChat = onOpenInChat,
    )
}
