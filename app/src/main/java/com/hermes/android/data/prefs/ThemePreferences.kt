package com.hermes.android.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hermes.android.presentation.ui.theme.HermesThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

private val Context.dataStore by preferencesDataStore(name = "hermes_theme")

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val key = stringPreferencesKey("theme_mode")

    fun observe(): Flow<HermesThemeMode> =
        context.dataStore.data.map { prefs ->
            prefs[key]?.let { runCatching { HermesThemeMode.valueOf(it) }.getOrNull() }
                ?: HermesThemeMode.DARK
        }

    suspend fun save(mode: HermesThemeMode) {
        context.dataStore.edit { it[key] = mode.name }
    }
}
