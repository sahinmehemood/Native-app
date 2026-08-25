package com.hermes.android.feature.automations

import com.hermes.android.core.navigation.HermesDestination
import com.hermes.android.core.navigation.Route
import org.koin.dsl.module

fun automationsModule() = module { }

fun automationsDestination(): HermesDestination =
    HermesDestination(Route.Automations.route) { AutomationsScreen() }
