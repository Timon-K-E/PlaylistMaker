package com.practicum.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.library.domain.FavoritesState

class FavoritesViewModel : ViewModel() {

    private val _state = MutableLiveData<FavoritesState>(FavoritesState.Empty)
    val state: LiveData<FavoritesState> = _state

}