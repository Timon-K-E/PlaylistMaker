package com.practicum.playlistmaker.player.domain

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    override fun preparePlayer(url: String, onPrepared: () -> Unit) {
        repository.prepare(url, onPrepared)
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