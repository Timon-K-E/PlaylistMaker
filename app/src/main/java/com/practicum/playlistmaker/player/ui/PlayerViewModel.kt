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

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    internal  val zeroTimeString by lazy { dateFormat.format(0) }
    private val stateLiveData = MutableLiveData<PlayerState>()
    fun observeState(): LiveData<PlayerState> = stateLiveData

    private val trackLiveData = MutableLiveData<Track>()
    fun observeTrack(): LiveData<Track> = trackLiveData

    init {
        trackLiveData.value = track
        preparePlayer()
    }

    private fun preparePlayer() {
        stateLiveData.value = PlayerState.Default

        playerInteractor.preparePlayer(track.previewUrl) {
            isPrepared = true
            stateLiveData.value = PlayerState.Prepared
        }

        playerInteractor.setOnCompletionListener {
            isPrepared = false
            stateLiveData.value = PlayerState.Completion
            stopTimer()
        }
    }

    fun playButtonClicked() {
        if (!isPrepared) {
            preparePlayer()
            return
        }
        playerInteractor.startPlayer()
        stateLiveData.value = PlayerState.Playing
        startTimer()
    }

    fun pauseButtonClicked() {
        playerInteractor.pausePlayer()
        stateLiveData.value = PlayerState.Paused
        stopTimer()
    }

    fun pausePlayer() {
        if (playerInteractor.isPlaying()) {
            playerInteractor.pausePlayer()
            stateLiveData.value = PlayerState.Paused
            stopTimer()
        }
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