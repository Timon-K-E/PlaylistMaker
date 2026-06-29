package com.practicum.playlistmaker.playlists.domain

sealed class AddTrackResult {
    object Success : AddTrackResult()
    object AlreadyExists : AddTrackResult()
}