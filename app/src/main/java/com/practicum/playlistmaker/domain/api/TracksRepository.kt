package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTrack(expression: String, callback: (List<Track>?, String?) -> Unit)
}