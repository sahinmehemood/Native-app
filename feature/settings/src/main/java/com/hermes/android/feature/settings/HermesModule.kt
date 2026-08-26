package com.hermes.android.feature.settings

import com.hermes.android.core.gateway.HermesGatewayClient
import com.hermes.android.core.navigation.HermesDestination
import com.hermes.android.core.navigation.Route
import com.hermes.android.feature.settings.SettingsScreen
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun settingsModule() = module {
    viewModel { SettingsViewModel(get(), get<HermesGatewayClient>()) }
}

/** Nav-graph entry for the settings screen. */
fun settingsDestination(onNavigateUp: () -> Unit): HermesDestination =
    HermesDestination(Route.Settings.route) { SettingsScreen(onNavigateUp = onNavigateUp) }
