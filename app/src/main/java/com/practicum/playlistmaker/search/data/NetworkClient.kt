package com.practicum.playlistmaker.search.data

interface NetworkClient {
    fun doRequest(dto: Any): Response
}