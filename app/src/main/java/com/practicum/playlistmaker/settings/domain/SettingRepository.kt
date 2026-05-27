package com.practicum.playlistmaker.settings.domain

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSettings(settings: ThemeSettings)
    fun applyCurrentTheme()
}