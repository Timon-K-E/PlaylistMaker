package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.practicum.playlistmaker.search.domain.Resource

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {

    override fun searchTrack(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            200 -> {
                val iTunesResponse = response as ITunesResponse
                emit(Resource.Success(iTunesResponse.results.map { TrackMapper.mapToTrack(it) }))
            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}