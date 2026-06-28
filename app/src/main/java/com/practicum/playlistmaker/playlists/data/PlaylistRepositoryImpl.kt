package com.practicum.playlistmaker.playlists.data

import com.practicum.playlistmaker.playlists.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.playlists.data.db.PlaylistDbConvertor
import com.practicum.playlistmaker.playlists.domain.Playlist
import com.practicum.playlistmaker.playlists.domain.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val convertor: PlaylistDbConvertor
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
}