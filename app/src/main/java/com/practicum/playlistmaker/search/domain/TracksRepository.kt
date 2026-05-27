package com.practicum.playlistmaker.search.domain

interface TracksRepository {
    fun searchTrack(expression: String, callback: (List<Track>?, String?) -> Unit)
}