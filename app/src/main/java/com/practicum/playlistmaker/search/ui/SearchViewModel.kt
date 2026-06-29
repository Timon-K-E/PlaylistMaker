package com.practicum.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.favorite.domain.FavoriteTrackInteractor
import kotlinx.coroutines.flow.collectLatest

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor
) : ViewModel() {
    private var searchJob: Job? = null
    private var clickJob: Job? = null
    private var progressJob: Job? = null
    private var isClickAllowed = true
    private var currentQuery = ""
    private var lastSearchResults: List<Track>? = null
    private var progressStartTime = 0L

    private var favoriteIdsFlowJob: Job? = null
    private val favoriteIds = mutableListOf<Long>()

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    init {
        loadHistory()
        subscribeToFavoriteIds()
    }

    private fun subscribeToFavoriteIds() {
        favoriteIdsFlowJob = viewModelScope.launch {
            favoriteTrackInteractor.getFavoriteIdsFlow()
                .collectLatest { ids ->
                    favoriteIds.clear()
                    favoriteIds.addAll(ids)
                    lastSearchResults?.let { tracks ->
                        val updatedTracks = updateFavoriteStatus(tracks)
                        stateLiveData.value = SearchState.Content(updatedTracks)
                        lastSearchResults = updatedTracks
                    }
                }
        }
    }

    private fun updateFavoriteStatus(tracks: List<Track>): List<Track> {
        return tracks.map { track ->
            track.copy(isFavorite = favoriteIds.contains(track.trackId))
        }
    }
    fun searchDebounce(query: String) {
        if (currentQuery == query && lastSearchResults != null) {
            return
        }
        currentQuery = query

        searchJob?.cancel()

        if (query.isEmpty()) {
            lastSearchResults = null
            loadHistory()
        } else {
            progressStartTime = System.currentTimeMillis()
            stateLiveData.value = SearchState.Loading
            searchJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY)
                performSearch()
            }
        }
    }

    private suspend  fun performSearch() {
        if (currentQuery.isEmpty()) return

        var foundTracks: List<Track>? = null
        var errorMessage: String? = null

        tracksInteractor.searchTracks(currentQuery)
            .collect { result ->
                result.onSuccess { tracks ->
                    val enrichedTracks = updateFavoriteStatus(tracks)
                    foundTracks = tracks
                }.onFailure { exception ->
                    errorMessage = exception.message
                }
            }

        val elapsedTime = System.currentTimeMillis() - progressStartTime
        val remainingTime = PROGRESS_MIN_DISPLAY_TIME - elapsedTime

        if (remainingTime > 0) {
            delay(remainingTime)
        }
        processSearchResult(foundTracks, errorMessage)
    }

    private fun processSearchResult(foundTracks: List<Track>?, errorMessage: String?) {
        when {
            errorMessage != null -> {
                lastSearchResults = null
                stateLiveData.value = SearchState.Error
            }
            foundTracks.isNullOrEmpty() -> {
                lastSearchResults = null
                stateLiveData.value = SearchState.Empty
            }
            else -> {
                lastSearchResults = foundTracks
                stateLiveData.value = SearchState.Content(foundTracks)
            }
        }
    }

    fun onTrackClick(track: Track) {
        if (clickDebounce()) {
            searchHistoryInteractor.addTrackToHistory(track)
            if (currentQuery.isEmpty()) {
                loadHistory()
            }
        }
    }

    fun onClearButtonClick() {
        currentQuery = ""
        lastSearchResults = null
        loadHistory()
    }

    fun onClearHistoryClick() {
        searchHistoryInteractor.clearHistory()
        if (currentQuery.isEmpty()) {
            loadHistory()
        }
    }

    fun loadHistory() {
        if (currentQuery.isEmpty()) {
            val history = searchHistoryInteractor.getHistory()
            val enrichedHistory = updateFavoriteStatus(history)
            if (history.isNotEmpty()) {
                stateLiveData.value = SearchState.History(history)
            } else {
                stateLiveData.value = SearchState.Content(emptyList())
            }
        }
    }

    fun restoreStateIfNeeded() {
        if (currentQuery.isNotEmpty() && lastSearchResults != null) {
            stateLiveData.value = SearchState.Content(lastSearchResults!!)
        } else if (currentQuery.isNotEmpty() && stateLiveData.value !is SearchState.Content) {
            searchDebounce(currentQuery)
        } else if (currentQuery.isEmpty()) {
            loadHistory()
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            clickJob?.cancel()
            clickJob = viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    override fun onCleared() {
        searchJob?.cancel()
        clickJob?.cancel()
        progressJob?.cancel()
        favoriteIdsFlowJob?.cancel()
        super.onCleared()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val PROGRESS_MIN_DISPLAY_TIME = 500L
    }
}