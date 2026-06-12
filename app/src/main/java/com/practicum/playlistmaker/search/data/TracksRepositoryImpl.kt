package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {

    override fun searchTrack(expression: String): Flow<Result<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        when (response.resultCode) {
            -1 -> {
                emit(Result.failure(IOException("Проверьте подключение к интернету")))
            }

            200 -> {
                val iTunesResponse = response as ITunesResponse
                val tracks = iTunesResponse.results.map { TrackMapper.mapToTrack(it) }
                emit(Result.success(tracks))
            }

            else -> {
                emit(Result.failure(Exception("Ошибка сервера")))
            }
        }
    }
}