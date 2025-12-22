package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track(
//    val trackName: String,
//    val artistName: String,
//    val trackTime: String,
//    val artworkUrl100: String
    @SerializedName("trackName") val trackName: String, // Название композиции
    @SerializedName("artistName") val artistName: String, //Имя исполнителя
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long, // Время трека в миллисекундах
    @SerializedName("artworkUrl100") val artworkUrl100: String //Ссылка на изображение обложки
)