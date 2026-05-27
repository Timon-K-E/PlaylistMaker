package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SettingRepository
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.models.ThemeSettings

class SettingsInteractorImpl(private val repository: SettingRepository) : SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings = repository.getThemeSettings()
    override fun updateThemeSettings(settings: ThemeSettings) = repository.updateThemeSettings(settings)
    override fun applyCurrentTheme() = repository.applyCurrentTheme()
}