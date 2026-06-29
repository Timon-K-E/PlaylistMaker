package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.newplaylist.ui.NewPlaylistViewModel
import com.practicum.playlistmaker.favorite.ui.FavoritesViewModel
import com.practicum.playlistmaker.playlists.ui.PlaylistsViewModel
import com.practicum.playlistmaker.player.ui.PlayerViewModel
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(
            tracksInteractor = get(),
            searchHistoryInteractor = get(),
            favoriteTrackInteractor = get()
        )
    }

    viewModel { params ->
        PlayerViewModel(
            playerInteractor = get(),
            favoriteTrackInteractor = get(),
            playlistInteractor = get(),
            track = params.get()
        )
    }

    viewModel {
        SettingsViewModel(get())
    }

    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get())
    }
}