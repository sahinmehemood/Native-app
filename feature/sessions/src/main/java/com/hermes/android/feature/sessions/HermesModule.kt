package com.hermes.android.feature.sessions

import com.hermes.android.core.navigation.HermesDestination
import com.hermes.android.core.navigation.Route
import org.koin.dsl.module

fun sessionsModule() = module { }

fun sessionsDestination(): HermesDestination =
    HermesDestination(Route.Sessions.route) { SessionsScreen() }
