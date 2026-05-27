package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.PlayerRepository
import org.koin.dsl.module

val playerModule = module {

    factory<PlayerRepository> { PlayerRepositoryImpl(get()) }
    factory<PlayerInteractor> { PlayerInteractorImpl(get()) }
}