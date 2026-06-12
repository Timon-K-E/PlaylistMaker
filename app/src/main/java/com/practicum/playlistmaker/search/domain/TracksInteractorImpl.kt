package com.practicum.playlistmaker.search.domain

import kotlinx.coroutines.flow.Flow

class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(
        expression: String
    ): Flow<Resource<List<Track>>> {
        return repository.searchTrack(expression)
    }
}