package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.favorite.ui.FavoritesViewModel
import com.practicum.playlistmaker.playlists.ui.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val libraryModule = module {

    viewModel {
        FavoritesViewModel(
            favoriteTrackInteractor = get()
        )
    }


}