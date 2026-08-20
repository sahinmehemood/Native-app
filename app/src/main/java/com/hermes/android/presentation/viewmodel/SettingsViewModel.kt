package com.hermes.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.data.prefs.ThemePreferences
import com.hermes.android.domain.usecase.GetConnectionUseCase
import com.hermes.android.presentation.ui.theme.HermesThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getConnection: GetConnectionUseCase,
    private val themePreferences: ThemePreferences
) : ViewModel() {
    val connection: Flow<com.hermes.android.domain.model.ConnectionConfig?> = getConnection()
    val themeMode: Flow<HermesThemeMode> = themePreferences.observe()

    fun setThemeMode(mode: HermesThemeMode) {
        viewModelScope.launch { themePreferences.save(mode) }
    }
}
