package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun getHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearHistory()
}