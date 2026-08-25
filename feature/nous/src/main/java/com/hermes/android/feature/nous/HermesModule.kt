package com.hermes.android.feature.nous

import com.hermes.android.core.navigation.HermesDestination
import com.hermes.android.core.navigation.Route
import org.koin.dsl.module

fun nousModule() = module { }

fun nousDestination(): HermesDestination =
    HermesDestination(Route.Nous.route) { NousScreen() }
