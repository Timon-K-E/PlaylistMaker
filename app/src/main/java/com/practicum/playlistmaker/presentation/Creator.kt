package com.practicum.playlistmaker.presentation

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.practicum.playlistmaker.data.network.ITunesApiService
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.repository.*
import com.practicum.playlistmaker.domain.api.*
import com.practicum.playlistmaker.domain.impl.*
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

    private fun getTracksRepository(): TracksRepository {
        val iTunesService = getITunesApiService()
        val networkClient = RetrofitNetworkClient(appContext, iTunesService)
        return TracksRepositoryImpl(networkClient)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        val sharedPreferences = getSharedPreferences()
        return SearchHistoryInteractorImpl(SearchHistoryRepositoryImpl(sharedPreferences))
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        val sharedPreferences = getSharedPreferences()
        return SettingsInteractorImpl(SettingsRepositoryImpl(sharedPreferences))
    }

    fun providePlayerInteractor(): PlayerInteractor {
        val mediaPlayer = getMediaPlayer()
        return PlayerInteractorImpl(PlayerRepositoryImpl(mediaPlayer))
    }
}