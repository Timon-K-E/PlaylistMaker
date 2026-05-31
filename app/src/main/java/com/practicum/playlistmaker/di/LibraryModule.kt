package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.library.ui.FavoritesViewModel
import com.practicum.playlistmaker.library.ui.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val libraryModule = module {
    viewModel { PlaylistsViewModel() }
    viewModel { FavoritesViewModel() }
}