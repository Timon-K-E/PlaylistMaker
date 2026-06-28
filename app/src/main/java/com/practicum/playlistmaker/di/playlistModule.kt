package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.playlists.data.PlaylistRepositoryImpl
import com.practicum.playlistmaker.playlists.data.db.PlaylistDbConvertor
import com.practicum.playlistmaker.playlists.domain.PlaylistInteractor
import com.practicum.playlistmaker.playlists.domain.PlaylistInteractorImpl
import com.practicum.playlistmaker.playlists.domain.PlaylistRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val playlistModule = module {

    single { get<com.practicum.playlistmaker.favorite.data.db.AppDatabase>().playlistDao() }

    factory { PlaylistDbConvertor(get()) }

    factory<PlaylistRepository> { PlaylistRepositoryImpl(get(), get()) }

    factory<PlaylistInteractor> { PlaylistInteractorImpl(get()) }
}