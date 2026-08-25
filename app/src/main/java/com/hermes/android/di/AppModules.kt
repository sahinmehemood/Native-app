package com.hermes.android.di

import com.hermes.android.core.data.dataModule
import com.hermes.android.feature.activity.activityModule
import com.hermes.android.feature.automations.automationsModule
import com.hermes.android.feature.chat.chatModule
import com.hermes.android.feature.home.homeModule
import com.hermes.android.feature.nous.nousModule
import com.hermes.android.feature.sessions.sessionsModule
import com.hermes.android.feature.settings.settingsModule
import org.koin.dsl.module

/**
 * Root Koin graph for the app shell. Pulls in the data layer (which transitively
 * includes the gateway module) and every feature module, so a single
 * `startKoin { modules(appModule()) }` wires the whole client.
 */
fun appModule() = module {
    includes(
        dataModule(),
        homeModule(),
        chatModule(),
        settingsModule(),
        activityModule(),
        sessionsModule(),
        nousModule(),
        automationsModule(),
    )
}
