package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.favorite.domain.db.FavoriteTrackInteractor
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository,
    private val favoriteTrackInteractor: FavoriteTrackInteractor
) : SearchHistoryInteractor {
    override fun getHistory(): List<Track> {
        val history = repository.read()
        val favoriteIds = runBlocking {
            favoriteTrackInteractor.getFavoriteIdsFlow().first()
        }
        return history.map { track ->
            track.copy(isFavorite = favoriteIds.contains(track.trackId))
        }
    }
    override fun addTrackToHistory(track: Track) = repository.add(track)
    override fun clearHistory() = repository.clear()
}