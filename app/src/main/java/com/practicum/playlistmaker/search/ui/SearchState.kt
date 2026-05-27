package com.practicum.playlistmaker.search.ui

import com.practicum.playlistmaker.search.domain.Track

sealed class SearchState {
    object Loading : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    object Empty : SearchState()
    object Error : SearchState()
    data class History(val tracks: List<Track>) : SearchState()
}