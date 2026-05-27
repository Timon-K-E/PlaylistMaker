package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.Track


object TrackHistoryMapper {
    fun mapToDto(track: Track): TrackHistoryDto {
        return TrackHistoryDto(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            formattedTime = track.formattedTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl.takeIf { it.isNotEmpty() }
        )
    }

    fun mapToTrack(dto: TrackHistoryDto): Track {
        return Track(
            trackId = dto.trackId,
            trackName = dto.trackName,
            artistName = dto.artistName,
            formattedTime = dto.formattedTime,
            trackTimeMillis = dto.trackTimeMillis,
            artworkUrl100 = dto.artworkUrl100,
            collectionName = dto.collectionName,
            releaseDate = dto.releaseDate,
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            previewUrl = dto.previewUrl ?: ""
        )
    }
}