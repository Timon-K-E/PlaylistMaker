package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.presentation.Creator

class App : Application() {
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        Creator.initialize(this)

        val settingsInteractor = Creator.provideSettingsInteractor()
        darkTheme = settingsInteractor.getThemeSettings().isDarkTheme

        settingsInteractor.applyCurrentTheme()
    }
}