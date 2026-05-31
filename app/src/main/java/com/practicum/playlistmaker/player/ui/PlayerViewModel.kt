package com.practicum.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.search.domain.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val track: Track
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
    private var isPrepared = false
    private var currentPlayerState: PlayerState = PlayerState.Default

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    internal val zeroTimeString by lazy { dateFormat.format(0) }
    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private val trackLiveData = MutableLiveData<Track>()
    fun observeTrack(): LiveData<Track> = trackLiveData

    init {
        trackLiveData.value = track
        preparePlayer()
    }

    private fun preparePlayer() {
        playerInteractor.preparePlayer(track.previewUrl) {
            isPrepared = true
            val currentPosition = playerInteractor.getCurrentPosition()
            if (playerInteractor.isPlaying()) {
                currentPlayerState = PlayerState.Playing
                stateLiveData.value = currentPlayerState
                stateLiveData.value = PlayerState.TimeUpdate(dateFormat.format(currentPosition))
                startTimer()
            } else if (currentPosition > 0) {
                currentPlayerState = PlayerState.Paused
                stateLiveData.value = currentPlayerState
                stateLiveData.value = PlayerState.TimeUpdate(dateFormat.format(currentPosition))
            } else {
                currentPlayerState = PlayerState.Prepared
                stateLiveData.value = currentPlayerState
                stateLiveData.value = PlayerState.TimeUpdate(zeroTimeString)
            }
        }

        playerInteractor.setOnCompletionListener {
            isPrepared = false
            currentPlayerState = PlayerState.Completion
            stateLiveData.value = currentPlayerState
            stopTimer()
        }
    }

    fun playButtonClicked() {
        if (!isPrepared) {
            preparePlayer()
            return
        }
        playerInteractor.startPlayer()
        currentPlayerState = PlayerState.Playing
        stateLiveData.value = currentPlayerState
        startTimer()
    }

    fun pauseButtonClicked() {
        playerInteractor.pausePlayer()
        currentPlayerState = PlayerState.Paused
        stateLiveData.value = currentPlayerState
        stopTimer()
        val currentPosition = playerInteractor.getCurrentPosition()
        stateLiveData.value = PlayerState.TimeUpdate(dateFormat.format(currentPosition))
    }

    fun pausePlayer() {
        if (playerInteractor.isPlaying()) {
            playerInteractor.pausePlayer()
            currentPlayerState = PlayerState.Paused
            stateLiveData.value = currentPlayerState
            stopTimer()
            val currentPosition = playerInteractor.getCurrentPosition()
            stateLiveData.value = PlayerState.TimeUpdate(dateFormat.format(currentPosition))
        }
    }

    fun getCurrentPosition(): String {
        return dateFormat.format(playerInteractor.getCurrentPosition())
    }

    fun getCurrentPositionMillis(): Int {
        return playerInteractor.getCurrentPosition()
    }

    fun isPlaying(): Boolean {
        return playerInteractor.isPlaying()
    }

    private fun startTimer() {
        stopTimer()
        timerRunnable = object : Runnable {
            override fun run() {
                if (playerInteractor.isPlaying()) {
                    val currentPosition = playerInteractor.getCurrentPosition()
                    val formattedTime = dateFormat.format(currentPosition)
                    stateLiveData.value = PlayerState.TimeUpdate(formattedTime)
                    handler.postDelayed(this, UPDATE_DELAY)
                } else {
                    stopTimer()
                }
            }
        }
        handler.post(timerRunnable!!)
    }

    private fun stopTimer() {
        timerRunnable?.let { handler.removeCallbacks(it) }
        timerRunnable = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        playerInteractor.releasePlayer()
    }

    companion object {
        private const val UPDATE_DELAY = 200L
    }
}