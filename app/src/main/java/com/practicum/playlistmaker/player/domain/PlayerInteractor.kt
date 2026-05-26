package com.practicum.playlistmaker.player.domain

interface PlayerInteractor {
    fun preparePlayer(url: String, onPrepared: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
    fun isPlaying(): Boolean
    fun setOnCompletionListener(listener: () -> Unit)
}