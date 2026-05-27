package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun read(): List<Track>
    fun add(track: Track)
    fun clear()
}