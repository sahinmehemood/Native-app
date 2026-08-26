package com.hermes.android.feature.activity

import com.hermes.android.core.navigation.HermesDestination
import com.hermes.android.core.navigation.Route
import com.hermes.android.feature.activity.ui.ActivityRoute
import com.hermes.android.feature.activity.viewmodel.ActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun activityModule() = module {
    viewModel { ActivityViewModel(get()) }
}

fun activityDestination(
    onNavigateUp: () -> Unit,
    onOpenSession: (String) -> Unit,
): HermesDestination = HermesDestination(Route.Activity.route) {
    ActivityRoute(onNavigateUp = onNavigateUp, onOpenSession = onOpenSession)
}
