package com.practicum.playlistmaker.favorite.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.favorite.domain.FavoriteTrackInteractor
import com.practicum.playlistmaker.favorite.domain.FavoriteState
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoriteTrackInteractor: FavoriteTrackInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoriteState>(FavoriteState.Empty)
    val state: LiveData<FavoriteState> = _state

    init {
        fillData()
    }

    fun fillData() {
        viewModelScope.launch {
            favoriteTrackInteractor
                .getFavoriteTracks()
                .collectLatest { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            _state.postValue(FavoriteState.Empty)
        } else {
            _state.postValue(FavoriteState.Content(tracks))
        }
    }
}