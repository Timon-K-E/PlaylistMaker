package com.practicum.playlistmaker.favorite.domain.db

import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val favoriteTrackRepository: FavoriteTrackRepository
): FavoriteTrackInteractor {
    override suspend fun addTrackToFavorites(track: Track) {
        favoriteTrackRepository.addTrackToFavorites(track)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        favoriteTrackRepository.removeTrackFromFavorites(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTrackRepository.getFavoriteTracks()
    }

    override suspend fun getAllFavoriteIds(): List<Long> {
        return favoriteTrackRepository.getAllFavoriteIds()
    }

    override fun getFavoriteIdsFlow(): Flow<List<Long>> {
        return favoriteTrackRepository.getFavoriteIdsFlow()
    }

    override suspend fun isTrackFavorite(trackId: Long): Boolean {
        return favoriteTrackRepository.isTrackFavorite(trackId)
    }

}