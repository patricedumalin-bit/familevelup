package com.slydroidstudio.familevelup.ui.theme

import android.content.Context
import com.slydroidstudio.familevelup.security.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeManager {
    private val _currentTheme = MutableStateFlow(GalaxyTheme.OCEAN)
    val currentTheme: StateFlow<GalaxyTheme> = _currentTheme.asStateFlow()

    fun init(context: Context) {
        val prefs = PreferencesManager(context)
        _currentTheme.value = try {
            GalaxyTheme.valueOf(prefs.selectedTheme)
        } catch (_: Exception) {
            GalaxyTheme.OCEAN
        }
    }

    fun setTheme(context: Context, theme: GalaxyTheme) {
        _currentTheme.value = theme
        PreferencesManager(context).selectedTheme = theme.name
    }
}
