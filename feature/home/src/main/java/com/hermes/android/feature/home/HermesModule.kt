package com.hermes.android.feature.home

import com.hermes.android.core.gateway.di.gatewayModule
import com.hermes.android.feature.home.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun homeModule() = module {
    includes(gatewayModule())
    viewModel { HomeViewModel(get(), get()) }
}
