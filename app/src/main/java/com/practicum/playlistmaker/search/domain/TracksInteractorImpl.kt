package com.practicum.playlistmaker.search.domain

import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TrackConsumer) {
        executor.execute {
            repository.searchTrack(expression) { tracks, errorMessage ->
                consumer.consume(tracks, errorMessage)
            }
        }
    }
}