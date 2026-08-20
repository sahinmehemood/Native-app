package com.hermes.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hermes.android.presentation.navigation.HermesAppNavHost
import com.hermes.android.presentation.ui.theme.HermesTheme
import com.hermes.android.presentation.ui.theme.HermesThemeMode
import com.hermes.android.presentation.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeVm: SettingsViewModel = hiltViewModel()
            val themeMode by themeVm.themeMode.collectAsStateWithLifecycle(initialValue = HermesThemeMode.DARK)
            HermesTheme(mode = themeMode) {
                val navController = rememberNavController()
                HermesAppNavHost(navController = navController, startDestination = "splash")
            }
        }
    }
}
