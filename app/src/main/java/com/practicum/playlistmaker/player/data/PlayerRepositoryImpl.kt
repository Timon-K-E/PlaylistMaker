package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.PlayerRepository

class PlayerRepositoryImpl(
    private var mediaPlayer: MediaPlayer
) : PlayerRepository {

    private var isReleased = false

    override fun prepare(url: String, onPrepared: () -> Unit) {
        if (isReleased) {
            mediaPlayer = MediaPlayer()
            isReleased = false
        }
        mediaPlayer.apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener { onPrepared() }
        }
    }

    override fun start() {
        if (!isReleased) {
            mediaPlayer.start()
        }
    }

    override fun pause() {
        if (!isReleased) {
            mediaPlayer.pause()
        }
    }

    override fun release() {
        if (!isReleased) {
            mediaPlayer.release()
            isReleased = true
        }
    }

    override fun getCurrentPosition(): Int {
        return if (!isReleased) mediaPlayer.currentPosition else 0
    }

    override fun isPlaying(): Boolean {
        return if (!isReleased) mediaPlayer.isPlaying else false
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        if (!isReleased) {
            mediaPlayer.setOnCompletionListener { listener() }
        }
    }
}