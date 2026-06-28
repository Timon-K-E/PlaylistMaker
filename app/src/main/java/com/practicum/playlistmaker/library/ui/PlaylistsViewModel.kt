package com.practicum.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.PlaylistsState
import com.practicum.playlistmaker.playlists.domain.Playlist
import com.practicum.playlistmaker.playlists.domain.PlaylistInteractor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistsState>(PlaylistsState.Empty)
    val state: LiveData<PlaylistsState> = _state

    private val _playlists = MutableLiveData<List<Playlist>>(emptyList())
    val playlists: LiveData<List<Playlist>> = _playlists

    init {
        loadPlaylists()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists()
                .collectLatest { playlists ->
                    _playlists.value = playlists
                    if (playlists.isEmpty()) {
                        _state.value = PlaylistsState.Empty
                    } else {
                        _state.value = PlaylistsState.Content
                    }
                }
        }
    }
}