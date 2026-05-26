package com.practicum.playlistmaker.player.domain

sealed class PlayerState {
    object Default : PlayerState()
    object Prepared : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
    data class TimeUpdate(val currentTime: String) : PlayerState()
    object Completion : PlayerState()
}