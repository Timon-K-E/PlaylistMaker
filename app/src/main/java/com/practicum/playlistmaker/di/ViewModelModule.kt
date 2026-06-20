package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.ui.FavoritesViewModel
import com.practicum.playlistmaker.player.ui.PlayerViewModel
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
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
            track = params.get()
        )
    }

    viewModel {
        SettingsViewModel(get())
    }

    viewModel {
        FavoritesViewModel(get())
    }
}