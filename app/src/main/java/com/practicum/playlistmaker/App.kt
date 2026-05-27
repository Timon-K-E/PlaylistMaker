package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.creator.Creator

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Creator.initialize(this)

        val settingsInteractor = Creator.provideSettingsInteractor()
        settingsInteractor.applyCurrentTheme()
    }
}