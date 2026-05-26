package com.practicum.playlistmaker.player.domain

interface PlayerRepository {
    fun prepare(url: String, onPrepared: () -> Unit)
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun setOnCompletionListener(listener: () -> Unit)
}