package com.practicum.playlistmaker.library.domain

sealed interface PlaylistsState {
    object Empty : PlaylistsState
    object Content : PlaylistsState
}