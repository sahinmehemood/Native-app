package com.hermes.android.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.domain.model.ConnectionConfig
import com.hermes.android.domain.model.ConnectionMode
import com.hermes.android.domain.usecase.SaveConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val saveConnection: SaveConnectionUseCase
) : ViewModel() {
    private val _mode = MutableStateFlow(ConnectionMode.LOCAL)
    val mode: StateFlow<ConnectionMode> = _mode.asStateFlow()

    private val _url = MutableStateFlow("http://127.0.0.1:8642")
    val url: StateFlow<String> = _url.asStateFlow()

    private val _key = MutableStateFlow("")
    val key: StateFlow<String> = _key.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved.asStateFlow()

    fun setMode(m: ConnectionMode) {
        _mode.value = m
        if (m == ConnectionMode.REMOTE && _url.value.isBlank()) _url.value = "https://"
    }

    fun setUrl(u: String) { _url.value = u }
    fun setKey(k: String) { _key.value = k }

    fun save() {
        val base = _url.value.trim().removeSuffix("/")
        if (base.isBlank()) return
        viewModelScope.launch {
            saveConnection(ConnectionConfig(mode = _mode.value, baseUrl = base, apiKey = _key.value.trim()))
            _saved.value = true
        }
    }
}
