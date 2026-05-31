package com.practicum.playlistmaker.settings.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.ThemeSettings

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
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
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(null)
        super.onCleared()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 2000L
    }
}