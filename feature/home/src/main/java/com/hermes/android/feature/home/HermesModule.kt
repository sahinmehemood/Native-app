package com.hermes.android.feature.home

import com.hermes.android.core.navigation.HermesDestination
import com.hermes.android.core.navigation.Route
import com.hermes.android.feature.home.ui.HomeRoute
import com.hermes.android.feature.home.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Home feature module. The gateway client + approval store are provided by
 * [com.hermes.android.core.data.dataModule] (which includes the gateway module),
 * so they resolve globally without re-including here.
 */
fun homeModule() = module {
    viewModel { HomeViewModel(get(), get()) }
}

/** Nav-graph entry for the home dashboard. */
fun homeDestination(
    onNavigateToChat: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
): HermesDestination = HermesDestination(Route.Home.route) {
    HomeRoute(onSessionClick = onNavigateToChat, onSettingsClick = onNavigateToSettings)
}
