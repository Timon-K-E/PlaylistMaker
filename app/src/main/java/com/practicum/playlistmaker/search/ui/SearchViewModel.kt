package com.practicum.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.TracksInteractor
import com.practicum.playlistmaker.search.domain.Track

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch() }
    private var isClickAllowed = true
    private var currentQuery = ""
    private var lastSearchResults: List<Track>? = null
    private var progressStartTime = 0L

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    init {
        loadHistory()
    }

    fun searchDebounce(query: String) {
        if (currentQuery == query && lastSearchResults != null) {
            return
        }
        currentQuery = query

        handler.removeCallbacks(searchRunnable)

        if (query.isEmpty()) {
            lastSearchResults = null
            loadHistory()
        } else {
            progressStartTime = System.currentTimeMillis()
            stateLiveData.value = SearchState.Loading
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }

    private fun performSearch() {
        if (currentQuery.isEmpty()) return

        tracksInteractor.searchTracks(
            currentQuery,
            object : TracksInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post {
                        val elapsedTime = System.currentTimeMillis() - progressStartTime
                        val remainingTime = PROGRESS_MIN_DISPLAY_TIME - elapsedTime

                        if (remainingTime > 0) {
                            handler.postDelayed({
                                processSearchResult(foundTracks, errorMessage)
                            }, remainingTime)
                        } else {
                            processSearchResult(foundTracks, errorMessage)
                        }
                    }
                }
            })
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

    fun getCurrentQuery(): String = currentQuery

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(null)
        super.onCleared()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val PROGRESS_MIN_DISPLAY_TIME = 500L
    }
}