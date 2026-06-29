package com.practicum.playlistmaker.playlists.domain

sealed interface PlaylistsState {
    object Empty : PlaylistsState
    object Content : PlaylistsState
}