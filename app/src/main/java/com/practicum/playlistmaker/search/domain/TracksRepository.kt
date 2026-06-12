package com.practicum.playlistmaker.search.domain

import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTrack(expression: String): Flow<Result<List<Track>>>
}