package com.hermes.android.feature.chat

import com.hermes.android.core.gateway.di.gatewayModule
import com.hermes.android.feature.chat.domain.DraftRepository
import com.hermes.android.feature.chat.domain.InMemoryDraftRepository
import com.hermes.android.feature.chat.viewmodel.ChatViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun chatModule() = module {
    includes(gatewayModule())
    single<DraftRepository> { InMemoryDraftRepository() }
    viewModel { params -> ChatViewModel(get(), get(), get(), sessionId = params.getOrNull<String>() ?: "session-unknown") }
}
