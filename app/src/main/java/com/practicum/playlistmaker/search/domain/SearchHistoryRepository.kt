package com.practicum.playlistmaker.search.domain

interface SearchHistoryRepository {
    fun read(): List<Track>
    fun add(track: Track)
    fun clear()
}