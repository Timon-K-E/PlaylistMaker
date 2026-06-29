package com.practicum.playlistmaker.playlists.data.db

import com.google.gson.Gson
import com.practicum.playlistmaker.playlists.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.playlists.data.db.dao.TrackInPlaylistDao
import com.practicum.playlistmaker.playlists.data.db.entity.TrackInPlaylistEntity
import com.practicum.playlistmaker.playlists.domain.Playlist
import com.practicum.playlistmaker.playlists.domain.PlaylistRepository
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val trackInPlaylistDao: TrackInPlaylistDao,
    private val convertor: PlaylistDbConvertor,
    private val gson: Gson
) : PlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist): Long {
        val entity = convertor.map(playlist)
        return playlistDao.insertPlaylist(entity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = convertor.map(playlist)
        playlistDao.updatePlaylist(entity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists()
            .map { entities ->
                entities.map { convertor.map(it) }
            }
            .distinctUntilChanged()
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        val entity = playlistDao.getPlaylistById(id)
        return entity?.let { convertor.map(it) }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): Boolean {
        return try {
            val trackEntity = TrackInPlaylistEntity(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl100 = track.artworkUrl100,
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl
            )
            trackInPlaylistDao.insertTrack(trackEntity)

            val updatedTrackIds = playlist.trackIds.toMutableList()
            if (!updatedTrackIds.contains(track.trackId)) {
                updatedTrackIds.add(track.trackId)

                val updatedPlaylist = playlist.copy(
                    trackIds = updatedTrackIds,
                    trackCount = updatedTrackIds.size
                )

                updatePlaylist(updatedPlaylist)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}