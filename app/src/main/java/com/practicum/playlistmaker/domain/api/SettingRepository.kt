package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.ThemeSettings

interface SettingRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSettings(settings: ThemeSettings)
    fun applyCurrentTheme()
}