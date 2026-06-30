package com.practicum.playlistmaker

import android.app.Application
import com.practicum.playlistmaker.di.appModule
import com.practicum.playlistmaker.di.libraryModule
import com.practicum.playlistmaker.di.playerModule
import com.practicum.playlistmaker.di.viewModelModule
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.get


class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                appModule,
                playerModule,
                viewModelModule,
                libraryModule
            )
        }

        val settingsInteractor: SettingsInteractor = get(SettingsInteractor::class.java)
        settingsInteractor.applyCurrentTheme()
    }
}