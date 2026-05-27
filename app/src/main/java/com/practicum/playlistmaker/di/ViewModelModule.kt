package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.ui.PlayerViewModel
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel { params ->
        PlayerViewModel(get(), params.get())
    }

    viewModel {
        SettingsViewModel(get())
    }
}