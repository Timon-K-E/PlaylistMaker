package com.practicum.playlistmaker.favorite.domain

import com.practicum.playlistmaker.search.domain.Track

sealed interface FavoriteState {
    object Empty : FavoriteState
    data class Content(val tracks: List<Track>) : FavoriteState
}