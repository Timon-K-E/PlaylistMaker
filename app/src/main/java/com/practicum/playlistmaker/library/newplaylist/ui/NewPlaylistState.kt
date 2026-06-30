package com.practicum.playlistmaker.library.newplaylist.ui

import android.net.Uri

data class NewPlaylistState(
    val name: String = "",
    val description: String = "",
    val coverUri: Uri? = null,
    val isCreateButtonEnabled: Boolean = false,
    val createdPlaylistName: String? = null,
    val isLoading: Boolean = false
)