package com.practicum.playlistmaker


import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
class App : Application() {
    private lateinit var sharedPref: SharedPreferences

    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()

        sharedPref = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPref.getBoolean(SWITCH_THEME_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        sharedPref.edit {
            putBoolean(SWITCH_THEME_KEY, darkTheme)
        }

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_marker_preferences"
        const val SWITCH_THEME_KEY = "switch_theme_key"
    }
}