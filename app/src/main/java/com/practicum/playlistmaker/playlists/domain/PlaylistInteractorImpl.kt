// com/practicum/playlistmaker/playlists/domain/PlaylistInteractorImpl.kt
package com.practicum.playlistmaker.playlists.domain

import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun createPlaylist(playlist: Playlist): Long {
        return withContext(Dispatchers.IO) {
            repository.createPlaylist(playlist)
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        withContext(Dispatchers.IO) {
            repository.updatePlaylist(playlist)
        }
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        return withContext(Dispatchers.IO) {
            repository.getPlaylistById(id)
        }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist): AddTrackResult {
        return withContext(Dispatchers.IO) {
            if (playlist.trackIds.contains(track.trackId)) {
                return@withContext AddTrackResult.AlreadyExists
            }

            val success = repository.addTrackToPlaylist(track, playlist)

            return@withContext if (success) {
                AddTrackResult.Success
            } else {
                AddTrackResult.AlreadyExists
            }
        }
    }
}