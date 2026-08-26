package com.hermes.android.feature.nous

import com.hermes.android.core.navigation.HermesDestination
import com.hermes.android.core.navigation.Route
import com.hermes.android.feature.nous.ui.NousRoute
import com.hermes.android.feature.nous.viewmodel.NousViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun nousModule() = module {
    viewModel { NousViewModel(get()) }
}

fun nousDestination(
    onNavigateUp: () -> Unit,
    onOpenSession: (String) -> Unit,
): HermesDestination = HermesDestination(Route.Nous.route) {
    NousRoute(onNavigateUp = onNavigateUp, onOpenSession = onOpenSession)
}
