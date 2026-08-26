package com.hermes.android.feature.automations

import com.hermes.android.core.navigation.HermesDestination
import com.hermes.android.core.navigation.Route
import com.hermes.android.feature.automations.ui.AutomationsRoute
import com.hermes.android.feature.automations.viewmodel.AutomationsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun automationsModule() = module {
    viewModel { AutomationsViewModel(get(), get()) }
}

fun automationsDestination(
    onNavigateUp: () -> Unit,
    onOpenSession: (String) -> Unit,
): HermesDestination = HermesDestination(Route.Automations.route) {
    AutomationsRoute(onNavigateUp = onNavigateUp, onOpenSession = onOpenSession)
}
