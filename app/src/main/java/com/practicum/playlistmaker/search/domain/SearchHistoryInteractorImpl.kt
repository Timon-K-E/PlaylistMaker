package com.practicum.playlistmaker.search.domain

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) : SearchHistoryInteractor {
    override fun getHistory(): List<Track> = repository.read()
    override fun addTrackToHistory(track: Track) = repository.add(track)
    override fun clearHistory() = repository.clear()
}