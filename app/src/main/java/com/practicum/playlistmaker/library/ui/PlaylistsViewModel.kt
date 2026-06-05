package com.practicum.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.library.domain.PlaylistsState

class PlaylistsViewModel : ViewModel() {

    private val _state = MutableLiveData<PlaylistsState>(PlaylistsState.Empty)
    val state: LiveData<PlaylistsState> = _state

}