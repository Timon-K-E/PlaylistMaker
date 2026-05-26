package com.practicum.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.ThemeSettings

sealed class SettingsNavigationCommand {
    object ShareApp : SettingsNavigationCommand()
    object OpenSupport : SettingsNavigationCommand()
    object OpenUserAgreement : SettingsNavigationCommand()
}

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val themeLiveData = MutableLiveData<Boolean>()
    fun observeTheme(): LiveData<Boolean> = themeLiveData

    private val navigationCommandLiveData = MutableLiveData<SettingsNavigationCommand>()
    fun observeNavigationCommands(): LiveData<SettingsNavigationCommand> = navigationCommandLiveData

    init {
        themeLiveData.value = settingsInteractor.getThemeSettings().isDarkTheme
    }

    fun onThemeChanged(isChecked: Boolean) {
        settingsInteractor.updateThemeSettings(ThemeSettings(isChecked))
        themeLiveData.value = isChecked
    }

    fun shareApp() {
        navigationCommandLiveData.value = SettingsNavigationCommand.ShareApp
    }

    fun openSupport() {
        navigationCommandLiveData.value = SettingsNavigationCommand.OpenSupport
    }

    fun openUserAgreement() {
        navigationCommandLiveData.value = SettingsNavigationCommand.OpenUserAgreement
    }

    fun onNavigationCommandProcessed() {
        navigationCommandLiveData.value = null
    }
}