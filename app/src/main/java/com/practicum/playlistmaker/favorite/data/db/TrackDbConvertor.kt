package com.practicum.playlistmaker.favorite.data.db

import com.practicum.playlistmaker.favorite.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.Track

class TrackDbConvertor {

    fun map(track: Track, addedTimestamp: Long ): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            trackTimeMillis = track.trackTimeMillis,
            formattedTime = track.formattedTime,
            addedTimestamp = addedTimestamp
        )
    }

    fun map(trackEntity: TrackEntity): Track {
        return Track(
            trackId = trackEntity.trackId,
            artworkUrl100 = trackEntity.artworkUrl100,
            trackName = trackEntity.trackName,
            artistName = trackEntity.artistName,
            collectionName = trackEntity.collectionName,
            releaseDate = trackEntity.releaseDate,
            primaryGenreName = trackEntity.primaryGenreName,
            country = trackEntity.country,
            previewUrl = trackEntity.previewUrl,
            trackTimeMillis = trackEntity.trackTimeMillis,
            formattedTime = trackEntity.formattedTime,
            isFavorite = true
        )
    }
}