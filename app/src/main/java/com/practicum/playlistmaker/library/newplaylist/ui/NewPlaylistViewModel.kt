package com.practicum.playlistmaker.library.newplaylist.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.playlists.domain.Playlist
import com.practicum.playlistmaker.playlists.domain.PlaylistInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData(NewPlaylistState())
    val state: LiveData<NewPlaylistState> = _state

    private val _navigationEvent = MutableLiveData<NavigationEvent?>(null)
    val navigationEvent: LiveData<NavigationEvent?> = _navigationEvent
    private var isDataChanged = false

    fun updatePlaylistName(name: String) {
        val currentState = _state.value ?: return
        _state.value = currentState.copy(
            name = name,
            isCreateButtonEnabled = name.isNotBlank()
        )
        isDataChanged = true
    }

    fun updatePlaylistDescription(description: String) {
        val currentState = _state.value ?: return
        _state.value = currentState.copy(description = description)
        isDataChanged = true
    }

    fun updateCoverUri(uri: Uri?) {
        val currentState = _state.value ?: return
        _state.value = currentState.copy(coverUri = uri)
        isDataChanged = true
    }

    fun onCreatePlaylistClicked(context: Context) {
        val currentState = _state.value ?: return
        if (currentState.name.isBlank()) return

        viewModelScope.launch {
            _state.value = _state.value?.copy(isLoading = true)

            val coverPath = currentState.coverUri?.let { uri ->
                saveImageToInternalStorage(context, uri)
            }

            val playlist = Playlist(
                name = currentState.name,
                description = currentState.description.takeIf { it.isNotBlank() },
                coverPath = coverPath
            )

            playlistInteractor.createPlaylist(playlist)

            _state.value = _state.value?.copy(
                createdPlaylistName = currentState.name,
                isLoading = false
            )

            _navigationEvent.postValue(NavigationEvent.NavigateBack)
            isDataChanged = false
        }
    }

    private suspend fun saveImageToInternalStorage(context: Context, uri: Uri): String {
        return withContext(Dispatchers.IO) {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val fileName = "playlist_cover_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)

            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }

            file.absolutePath
        }
    }

    fun onBackPressed(): Boolean {
        return if (isDataChanged) {
            _navigationEvent.postValue(NavigationEvent.ShowExitDialog)
            true
        } else {
            _navigationEvent.postValue(NavigationEvent.NavigateBack)
            true
        }
    }

    fun onExitDialogConfirmed() {
        _navigationEvent.postValue(NavigationEvent.NavigateBack)
        isDataChanged = false
    }

    fun onExitDialogCancelled() {
        _navigationEvent.postValue(NavigationEvent.DismissDialog)
    }

    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    fun onToastShown() {
        val currentState = _state.value ?: return
        _state.value = currentState.copy(createdPlaylistName = null)
    }

    sealed class NavigationEvent {
        object NavigateBack : NavigationEvent()
        object ShowExitDialog : NavigationEvent()
        object DismissDialog : NavigationEvent()
    }
}