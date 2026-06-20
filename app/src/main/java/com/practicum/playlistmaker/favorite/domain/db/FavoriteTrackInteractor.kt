package com.practicum.playlistmaker.favorite.domain.db

import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackInteractor {
    suspend fun addTrackToFavorites(track: Track)
    suspend fun removeTrackFromFavorites(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun getAllFavoriteIds(): List<Long>

    fun getFavoriteIdsFlow(): Flow<List<Long>>
    suspend fun isTrackFavorite(trackId: Long): Boolean
}