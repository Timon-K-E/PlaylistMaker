package com.practicum.playlistmaker.settings.domain

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings = repository.getThemeSettings()
    override fun updateThemeSettings(settings: ThemeSettings) = repository.updateThemeSettings(settings)
    override fun applyCurrentTheme() = repository.applyCurrentTheme()
}