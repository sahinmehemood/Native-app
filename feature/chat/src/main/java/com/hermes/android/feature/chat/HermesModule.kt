package com.hermes.android.feature.chat

import com.hermes.android.core.navigation.HermesDestination
import com.hermes.android.core.navigation.Route
import com.hermes.android.feature.chat.domain.DraftRepository
import com.hermes.android.feature.chat.domain.InMemoryDraftRepository
import com.hermes.android.feature.chat.ui.ChatRoute
import com.hermes.android.feature.chat.viewmodel.ChatViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Chat feature module. The gateway client + approval store come from
 * [com.hermes.android.core.data.dataModule] (which includes the gateway module)
 * and resolve globally.
 */
fun chatModule() = module {
    single<DraftRepository> { InMemoryDraftRepository() }
    viewModel { params -> ChatViewModel(get(), get(), get(), sessionId = params.getOrNull<String>() ?: "session-unknown") }
}

/** Nav-graph entry for a chat session. Reads `sessionId` from the route args. */
fun chatDestination(): HermesDestination = HermesDestination(Route.Chat.createRoute("{sessionId}")) { navController ->
    val sessionId = navController.currentBackStackEntry
        ?.arguments?.getString(Route.Chat.ARG) ?: "session-unknown"
    ChatRoute(sessionId = sessionId)
}
