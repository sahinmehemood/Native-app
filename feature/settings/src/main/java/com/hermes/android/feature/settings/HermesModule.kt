package com.hermes.android.feature.settings

import com.hermes.android.core.navigation.HermesDestination
import com.hermes.android.core.navigation.Route
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun settingsModule() = module {
    viewModel { SettingsViewModel(get()) }
}

/** Nav-graph entry for the settings screen. */
fun settingsDestination(onNavigateUp: () -> Unit): HermesDestination =
    HermesDestination(Route.Settings.route) { SettingsScreen(onNavigateUp = onNavigateUp) }
