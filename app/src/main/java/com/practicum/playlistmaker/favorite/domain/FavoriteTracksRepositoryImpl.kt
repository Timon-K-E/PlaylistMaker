package com.practicum.playlistmaker.favorite.domain


import com.practicum.playlistmaker.favorite.data.db.dao.FavoriteTracksDao
import com.practicum.playlistmaker.favorite.data.db.TrackDbConvertor
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

class FavoriteTracksRepositoryImpl
    (
    private val favoriteTracksDao: FavoriteTracksDao,
    private val trackDbConvertor: TrackDbConvertor,

    ): FavoriteTrackRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        val trackEntity = trackDbConvertor.map(track,System.currentTimeMillis())
        favoriteTracksDao.insertTrack(trackEntity)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        val trackEntity = favoriteTracksDao.getTrackById(track.trackId)
        trackEntity?.let {
            favoriteTracksDao.deleteTrack(it)
        }

    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTracksDao.getAllTracks()
            .map { entities ->
                entities.map { trackDbConvertor.map(it) }
            }
            .distinctUntilChanged()
    }

    override suspend fun getAllFavoriteIds(): List<Long> {
        return favoriteTracksDao.getAllIdsFlow().first()
    }

    override fun getFavoriteIdsFlow(): Flow<List<Long>> {
        return favoriteTracksDao.getAllIdsFlow()
            .distinctUntilChanged()
    }

    override suspend fun isTrackFavorite(trackId: Long): Boolean {
        return favoriteTracksDao.getTrackById(trackId) != null
    }


}