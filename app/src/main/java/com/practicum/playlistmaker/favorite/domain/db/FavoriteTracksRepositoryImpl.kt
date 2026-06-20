package com.practicum.playlistmaker.favorite.domain.db


import com.practicum.playlistmaker.favorite.data.db.AppDatabase
import com.practicum.playlistmaker.favorite.data.db.TrackDbConvertor
import com.practicum.playlistmaker.favorite.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

class FavoriteTracksRepositoryImpl
    (
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,

    ): FavoriteTrackRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.favoriteTracksDao().insertTrack(trackEntity)
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        val trackEntity = appDatabase.favoriteTracksDao().getTrackById(track.trackId)
        trackEntity?.let {
            appDatabase.favoriteTracksDao().deleteTrack(it)
        }

    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.favoriteTracksDao().getAllTracks().map { tracks -> convertFromTrackEntity(tracks)
        }
    }

    override suspend fun getAllFavoriteIds(): List<Long> {
        return appDatabase.favoriteTracksDao().getAllIdsFlow().first()//
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map {  trackEntity -> trackDbConvertor.map(trackEntity) }
    }

    override fun getFavoriteIdsFlow(): Flow<List<Long>> {
        return appDatabase.favoriteTracksDao().getAllIdsFlow()
    }

    override suspend fun isTrackFavorite(trackId: Long): Boolean {
        return appDatabase.favoriteTracksDao().getTrackById(trackId) != null
    }


}