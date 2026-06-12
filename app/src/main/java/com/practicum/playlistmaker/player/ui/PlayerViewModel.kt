package com.practicum.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val track: Track
) : ViewModel() {

    private var timerJob: Job? = null

    private var isPrepared = false
    private var currentPlayerState: PlayerState = PlayerState.Default

    private val dateFormat by lazy {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }

    internal val zeroTimeString by lazy {
        dateFormat.format(0)
    }

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

            currentPlayerState = PlayerState.Prepared
            stateLiveData.value = currentPlayerState
            stateLiveData.value = PlayerState.TimeUpdate(zeroTimeString)
        }

        playerInteractor.setOnCompletionListener {
            currentPlayerState = PlayerState.Completion
            stateLiveData.value = currentPlayerState
            stopTimer()
        }
    }

    fun playButtonClicked() {

        when (currentPlayerState) {

            PlayerState.Completion -> {
                restartTrack()
            }

            else -> {
                if (isPrepared) {
                    playerInteractor.startPlayer()

                    currentPlayerState = PlayerState.Playing
                    stateLiveData.value = currentPlayerState

                    startTimer()
                }
            }
        }
    }

    private fun restartTrack() {

        playerInteractor.preparePlayer(track.previewUrl) {

            isPrepared = true

            playerInteractor.startPlayer()

            currentPlayerState = PlayerState.Playing
            stateLiveData.value = currentPlayerState

            startTimer()
        }
    }

    fun pauseButtonClicked() {
        playerInteractor.pausePlayer()

        currentPlayerState = PlayerState.Paused
        stateLiveData.value = currentPlayerState

        stopTimer()

        val currentPosition = playerInteractor.getCurrentPosition()

        stateLiveData.value =
            PlayerState.TimeUpdate(dateFormat.format(currentPosition))
    }

    fun pausePlayer() {
        if (playerInteractor.isPlaying()) {

            playerInteractor.pausePlayer()

            currentPlayerState = PlayerState.Paused
            stateLiveData.value = currentPlayerState

            stopTimer()

            val currentPosition = playerInteractor.getCurrentPosition()

            stateLiveData.value =
                PlayerState.TimeUpdate(dateFormat.format(currentPosition))
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

        timerJob = viewModelScope.launch {

            while (isActive && playerInteractor.isPlaying()) {

                val currentPosition =
                    playerInteractor.getCurrentPosition()

                stateLiveData.value =
                    PlayerState.TimeUpdate(
                        dateFormat.format(currentPosition)
                    )

                delay(UPDATE_DELAY)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
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