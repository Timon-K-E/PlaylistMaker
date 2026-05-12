package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerRepository

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    override fun preparePlayer(url: String, listener: PlayerInteractor.PlayerPreparedListener) {
        repository.prepare(url) { listener.onPrepared() }
    }

    override fun startPlayer() = repository.start()
    override fun pausePlayer() = repository.pause()
    override fun releasePlayer() = repository.release()
    override fun getCurrentPosition(): Int = repository.getCurrentPosition()
    override fun isPlaying(): Boolean = repository.isPlaying()
    override fun setOnCompletionListener(listener: () -> Unit) {
        repository.setOnCompletionListener(listener)
    }
}