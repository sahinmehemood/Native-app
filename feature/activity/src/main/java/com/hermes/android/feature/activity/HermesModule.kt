package com.hermes.android.feature.activity

import com.hermes.android.core.navigation.HermesDestination
import com.hermes.android.core.navigation.Route
import org.koin.dsl.module

fun activityModule() = module { }

fun activityDestination(): HermesDestination =
    HermesDestination(Route.Activity.route) { ActivityScreen() }
