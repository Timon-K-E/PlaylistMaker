package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.domain.TracksRepository

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTrack(expression: String, callback: (List<Track>?, String?) -> Unit) {
        val response = networkClient.doRequest(TrackSearchRequest(expression))

        when (response.resultCode) {
            -1 -> {
                callback(null, "Проверьте подключение к интернету")
            }

            200 -> {
                val iTunesResponse = response as ITunesResponse

                if (iTunesResponse.results.isNotEmpty()) {
                    val tracks = iTunesResponse.results.map { TrackMapper.mapToTrack(it) }
                    callback(tracks, null)
                } else {
                    callback(emptyList(), null)
                }
            }

            else -> {
                callback(null, "Ошибка сервера")
            }
        }
    }
}