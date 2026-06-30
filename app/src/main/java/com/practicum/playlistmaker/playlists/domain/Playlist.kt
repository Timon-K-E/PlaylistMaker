package com.practicum.playlistmaker.playlists.domain

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val coverPath: String? = null,
    val trackIds: List<Long> = emptyList(),
    val trackCount: Int = 0
)