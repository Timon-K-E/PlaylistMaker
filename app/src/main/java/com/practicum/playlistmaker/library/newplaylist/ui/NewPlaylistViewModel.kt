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

    private val _playlistName = MutableLiveData("")
    val playlistName: LiveData<String> = _playlistName

    private val _playlistDescription = MutableLiveData("")
    val playlistDescription: LiveData<String> = _playlistDescription

    private val _coverUri = MutableLiveData<Uri?>(null)
    val coverUri: LiveData<Uri?> = _coverUri

    private val _isCreateButtonEnabled = MutableLiveData(false)
    val isCreateButtonEnabled: LiveData<Boolean> = _isCreateButtonEnabled

    private val _navigationEvent = MutableLiveData<NavigationEvent?>(null)
    val navigationEvent: LiveData<NavigationEvent?> = _navigationEvent

    private val _showSuccessToast = MutableLiveData<String?>(null)
    val showSuccessToast: LiveData<String?> = _showSuccessToast

    private var isDataChanged = false

    fun updatePlaylistName(name: String) {
        _playlistName.value = name
        isDataChanged = true
        updateCreateButtonState()
    }

    fun updatePlaylistDescription(description: String) {
        _playlistDescription.value = description
        isDataChanged = true
    }

    fun updateCoverUri(uri: Uri?) {
        _coverUri.value = uri
        isDataChanged = true
    }

    private fun updateCreateButtonState() {
        val name = _playlistName.value ?: ""
        _isCreateButtonEnabled.value = name.isNotBlank()
    }

    fun onCreatePlaylistClicked(context: Context) {
        val name = _playlistName.value ?: return
        if (name.isBlank()) return

        viewModelScope.launch {
            val coverPath = _coverUri.value?.let { uri ->
                saveImageToInternalStorage(context, uri)
            }

            val playlist = Playlist(
                name = name,
                description = _playlistDescription.value?.takeIf { it.isNotBlank() },
                coverPath = coverPath
            )

            val id = playlistInteractor.createPlaylist(playlist)

            _showSuccessToast.postValue("Плейлист $name создан")
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
        _showSuccessToast.value = null
    }

    sealed class NavigationEvent {
        object NavigateBack : NavigationEvent()
        object ShowExitDialog : NavigationEvent()
        object DismissDialog : NavigationEvent()
    }
}