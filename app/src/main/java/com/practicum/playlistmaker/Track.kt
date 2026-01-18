package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("trackName") val trackName: String, // Название композиции
    @SerializedName("artistName") val artistName: String, //Имя исполнителя
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long, // Время трека в миллисекундах
    @SerializedName("artworkUrl100") val artworkUrl100: String, //Ссылка на изображение обложки
    @SerializedName("trackId") val trackId: Long,
    @SerializedName("collectionName") val collectionName: String, //альбом
    @SerializedName("releaseDate") val releaseDate: String,
    @SerializedName("country") val country: String, //страна
    @SerializedName("primaryGenreName") val primaryGenreName: String //Жанр
)