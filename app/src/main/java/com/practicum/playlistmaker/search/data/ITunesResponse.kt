package com.practicum.playlistmaker.search.data

data class ITunesResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()