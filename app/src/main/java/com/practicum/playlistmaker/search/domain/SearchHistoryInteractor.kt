package com.practicum.playlistmaker.search.domain

interface SearchHistoryInteractor {
    fun getHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearHistory()
}