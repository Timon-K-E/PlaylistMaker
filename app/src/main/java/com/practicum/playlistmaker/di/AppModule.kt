package com.practicum.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.favorite.data.db.AppDatabase
import com.practicum.playlistmaker.favorite.data.db.MIGRATION_1_2
import com.practicum.playlistmaker.favorite.data.db.MIGRATION_2_3
import com.practicum.playlistmaker.favorite.data.db.TrackDbConvertor
import com.practicum.playlistmaker.favorite.domain.FavoriteTrackInteractor
import com.practicum.playlistmaker.favorite.domain.FavoriteTrackRepository
import com.practicum.playlistmaker.favorite.domain.FavoriteTracksInteractorImpl
import com.practicum.playlistmaker.favorite.domain.FavoriteTracksRepositoryImpl
import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.PlayerRepository
import com.practicum.playlistmaker.playlists.data.db.PlaylistRepositoryImpl
import com.practicum.playlistmaker.playlists.data.db.PlaylistDbConvertor
import com.practicum.playlistmaker.playlists.domain.PlaylistInteractor
import com.practicum.playlistmaker.playlists.domain.PlaylistInteractorImpl
import com.practicum.playlistmaker.playlists.domain.PlaylistRepository
import com.practicum.playlistmaker.search.data.ITunesApiService
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.TracksRepository
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.sharing.data.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractorImpl
import com.practicum.playlistmaker.sharing.domain.SharingRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    single<ITunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(androidContext(), get())
    }

    single<SharedPreferences> {
        androidContext().getSharedPreferences("playlist_maker_preferences", Context.MODE_PRIVATE)
    }

    single { Gson() }

    factory<TracksRepository> { TracksRepositoryImpl(get()) }
    factory<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get(), get()) }
    factory<SettingsRepository> { SettingsRepositoryImpl(get()) }
    factory<SharingRepository> { SharingRepositoryImpl(androidContext()) }
    factory<TracksInteractor> { TracksInteractorImpl(get()) }
    factory<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(
            repository = get(),
            favoriteTrackInteractor = get()
        )
    }
    factory<SettingsInteractor> { SettingsInteractorImpl(get()) }
    factory<SharingInteractor> { SharingInteractorImpl(get()) }

    factory<PlayerRepository> { PlayerRepositoryImpl(MediaPlayer()) }
    factory<PlayerInteractor> { PlayerInteractorImpl(get()) }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "database.db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }

    single { get<AppDatabase>().favoriteTracksDao() }
    single { get<AppDatabase>().playlistDao() }
    single { get<AppDatabase>().trackInPlaylistDao() }

    single { TrackDbConvertor() }
    single { PlaylistDbConvertor(get()) }

    single<FavoriteTrackRepository> {
        FavoriteTracksRepositoryImpl(
            favoriteTracksDao = get(),
            trackDbConvertor = get()
        )
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(
            playlistDao = get(),
            trackInPlaylistDao = get(),
            convertor = get(),
            gson = get()
        )
    }

    single<FavoriteTrackInteractor> {
        FavoriteTracksInteractorImpl(get())
    }

    single<PlaylistInteractor> { PlaylistInteractorImpl(get()) }
}