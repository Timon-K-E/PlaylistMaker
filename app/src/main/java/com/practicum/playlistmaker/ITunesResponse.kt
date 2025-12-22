package com.practicum.playlistmaker

data class ITunesResponse (
    val resultCount: Int,
    val results: List<Track>
)