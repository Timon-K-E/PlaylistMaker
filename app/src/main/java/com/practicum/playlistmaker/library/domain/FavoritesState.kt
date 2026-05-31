package com.practicum.playlistmaker.library.domain

sealed interface FavoritesState {
    object Empty : FavoritesState
    object Content : FavoritesState
}