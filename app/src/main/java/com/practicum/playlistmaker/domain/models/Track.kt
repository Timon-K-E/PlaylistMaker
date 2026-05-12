package com.practicum.playlistmaker.domain.models

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val trackId: Long,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) : Serializable {
    val formattedTime: String
        get() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}