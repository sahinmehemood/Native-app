package com.hermes.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.domain.usecase.GetConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    getConnection: GetConnectionUseCase
) : ViewModel() {
    val configured: StateFlow<Boolean?> = getConnection()
        .map { it?.isConfigured == true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
