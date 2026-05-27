package com.practicum.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.google.gson.Gson
import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerInteractorImpl
import com.practicum.playlistmaker.search.data.ITunesApiService
import com.practicum.playlistmaker.search.data.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractorImpl
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.data.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private const val ITUNES_BASE_URL = "https://itunes.apple.com"
    private const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"

    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    private fun getITunesApiService(): ITunesApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ITunesApiService::class.java)
    }

    private fun getSharedPreferences(): SharedPreferences {
        return appContext.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    private fun getMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

    fun provideTracksInteractor(): TracksInteractor {
        val iTunesService = getITunesApiService()
        val networkClient = RetrofitNetworkClient(appContext, iTunesService)
        return TracksInteractorImpl(TracksRepositoryImpl(networkClient))
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        val sharedPreferences = getSharedPreferences()
        return SearchHistoryInteractorImpl(SearchHistoryRepositoryImpl(sharedPreferences, Gson()))
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        val sharedPreferences = getSharedPreferences()
        return SettingsInteractorImpl(SettingsRepositoryImpl(sharedPreferences))
    }

    fun providePlayerInteractor(): PlayerInteractor {
        val mediaPlayer = getMediaPlayer()
        return PlayerInteractorImpl(PlayerRepositoryImpl(mediaPlayer))
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(SharingRepositoryImpl(appContext))
    }
}