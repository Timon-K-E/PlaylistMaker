package com.practicum.playlistmaker.data.repository

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.domain.api.SettingRepository
import com.practicum.playlistmaker.domain.models.ThemeSettings
import androidx.core.content.edit

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingRepository {

    override fun getThemeSettings(): ThemeSettings {
        val isDark = sharedPreferences.getBoolean(SWITCH_THEME_KEY, false)
        return ThemeSettings(isDark)
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        sharedPreferences.edit { putBoolean(SWITCH_THEME_KEY, settings.isDarkTheme) }
        applyTheme(settings.isDarkTheme)
    }

    override fun applyCurrentTheme() {
        val isDark = sharedPreferences.getBoolean(SWITCH_THEME_KEY, false)
        applyTheme(isDark)
    }

    private fun applyTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object {
        const val SWITCH_THEME_KEY = "switch_theme_key"
    }
}