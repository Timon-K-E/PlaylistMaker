package com.practicum.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.ThemeSettings

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val themeLiveData = MutableLiveData<Boolean>()
    fun observeTheme(): LiveData<Boolean> = themeLiveData

    private val navigationCommandLiveData = MutableLiveData<Event<SettingsNavigationCommand>>()
    fun observeNavigationCommands(): LiveData<Event<SettingsNavigationCommand>> = navigationCommandLiveData

    init {
        themeLiveData.value = settingsInteractor.getThemeSettings().isDarkTheme
    }

    fun onThemeChanged(isChecked: Boolean) {
        settingsInteractor.updateThemeSettings(ThemeSettings(isChecked))
        themeLiveData.value = isChecked
    }

    fun shareApp() {
        navigationCommandLiveData.value = Event(SettingsNavigationCommand.ShareApp)
    }

    fun openSupport() {
        navigationCommandLiveData.value = Event(SettingsNavigationCommand.OpenSupport)
    }

    fun openUserAgreement() {
        navigationCommandLiveData.value = Event(SettingsNavigationCommand.OpenUserAgreement)
    }
}