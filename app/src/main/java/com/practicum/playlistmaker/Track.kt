package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Track(
    @SerializedName(IntentKeys.TRACK_NAME) val trackName: String, // Название композиции
    @SerializedName(IntentKeys.ARTIST_NAME) val artistName: String, //Имя исполнителя
    @SerializedName(IntentKeys.TRACK_TIME) val trackTimeMillis: Long, // Время трека в миллисекундах
    @SerializedName(IntentKeys.ARTWORK_URL) val artworkUrl100: String, //Ссылка на изображение обложки
    @SerializedName(IntentKeys.TRACK_ID) val trackId: Long,
    @SerializedName(IntentKeys.COLLECTION_NAME) val collectionName: String, //альбом
    @SerializedName(IntentKeys.RELEASE_DATE) val releaseDate: String,
    @SerializedName(IntentKeys.COUNTRY) val country: String, //страна
    @SerializedName(IntentKeys.PRIMARY_GENRE) val primaryGenreName: String, //Жанр
    @SerializedName(IntentKeys.PREVIEW_URL) val previewUrl: String

): Parcelable


