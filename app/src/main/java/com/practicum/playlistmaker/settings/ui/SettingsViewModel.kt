package com.practicum.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.ThemeSettings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private var clickJob: Job? = null
    private var isClickAllowed = true

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
        if (clickDebounce()) {
            navigationCommandLiveData.value = Event(SettingsNavigationCommand.ShareApp)
        }
    }

    fun openSupport() {
        if (clickDebounce()) {
            navigationCommandLiveData.value = Event(SettingsNavigationCommand.OpenSupport)
        }
    }

    fun openUserAgreement() {
        if (clickDebounce()) {
            navigationCommandLiveData.value = Event(SettingsNavigationCommand.OpenUserAgreement)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            clickJob?.cancel()
            clickJob = viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    override fun onCleared() {
        clickJob?.cancel()
        super.onCleared()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 2000L
    }
}