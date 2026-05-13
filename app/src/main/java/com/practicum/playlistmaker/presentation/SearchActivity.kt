package com.practicum.playlistmaker.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track

class SearchActivity : AppCompatActivity() {

    private companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val PROGRESS_MIN_DISPLAY_TIME = 500L
        const val SAVE_TEXT = "SAVE_TEXT"
        const val TEXT_DEF = ""
    }
    private var progressStartTime = 0L
    private var saveTextInput: String = TEXT_DEF

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var backButton: Button
    private lateinit var refreshButton: Button
    private lateinit var clearHistoryButton: Button

    private lateinit var recyclerViewTracks: RecyclerView
    private lateinit var recyclerViewHistory: RecyclerView

    private lateinit var placeholderNothingFound: LinearLayout
    private lateinit var placeholderNetworkError: LinearLayout
    private lateinit var historyLayout: LinearLayout
    private lateinit var progressBar: ProgressBar

    private val trackList = mutableListOf<Track>()
    private val historyList = mutableListOf<Track>()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private lateinit var tracksInteractor: TracksInteractor
    private val searchHistoryInteractor by lazy { Creator.provideSearchHistoryInteractor() }

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { performSearch() }
    private var isClickAllowed = true

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVE_TEXT, saveTextInput)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        tracksInteractor = Creator.provideTracksInteractor()

        initViews()
        setupAdapters()
        setupListeners()
        setupWindowInsets()

        if (savedInstanceState != null) {
            saveTextInput = savedInstanceState.getString(SAVE_TEXT, TEXT_DEF)
            searchEditText.setText(saveTextInput)
        }

        searchEditText.requestFocus()
    }

    override fun onResume() {
        super.onResume()
        refreshHistory()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun initViews() {
        backButton = findViewById(R.id.back_screen_search)
        clearButton = findViewById(R.id.clear_button)
        searchEditText = findViewById(R.id.search_edit_text)

        recyclerViewTracks = findViewById(R.id.recycler_view_track)
        recyclerViewHistory = findViewById(R.id.recycler_view_history)

        placeholderNothingFound = findViewById(R.id.placeholder_nothing_found)
        placeholderNetworkError = findViewById(R.id.placeholder_network_error)
        refreshButton = findViewById(R.id.refresh_button)

        historyLayout = findViewById(R.id.search_history)
        clearHistoryButton = findViewById(R.id.clear_button_history)
        progressBar = findViewById(R.id.progress_bar)
    }

    private fun setupAdapters() {
        trackAdapter = TrackAdapter(trackList) { track ->
            if (clickDebounce()) {
                searchHistoryInteractor.addTrackToHistory(track)
                openPlayer(track)
            }
        }
        recyclerViewTracks.layoutManager = LinearLayoutManager(this)
        recyclerViewTracks.adapter = trackAdapter

        historyAdapter = TrackAdapter(historyList) { track ->
            if (clickDebounce()) {
                searchHistoryInteractor.addTrackToHistory(track)
                refreshHistory()
                openPlayer(track)
            }
        }
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        recyclerViewHistory.adapter = historyAdapter
    }

    private fun setupListeners() {
        backButton.setOnClickListener { finish() }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            hideKeyboard()
            showState(SearchResult.CONTENT)
            historyLayout.isVisible = searchHistoryInteractor.getHistory().isNotEmpty()
        }

        refreshButton.setOnClickListener { performSearch() }

        clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            refreshHistory()
            historyLayout.isVisible = false
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            historyLayout.isVisible = hasFocus && searchEditText.text.isEmpty() && searchHistoryInteractor.getHistory().isNotEmpty()
        }

        searchEditText.doOnTextChanged { text, _, _, _ ->
            saveTextInput = text.toString()
            clearButton.isVisible = !text.isNullOrEmpty()

            handler.removeCallbacks(searchRunnable)

            if (saveTextInput.isEmpty()) {
                trackList.clear()
                trackAdapter.notifyDataSetChanged()
                progressBar.isVisible = false
                placeholderNothingFound.isVisible = false
                placeholderNetworkError.isVisible = false

                historyLayout.isVisible = searchHistoryInteractor.getHistory().isNotEmpty()
                if (historyLayout.isVisible) refreshHistory()
            } else {
                historyLayout.isVisible = false
                showState(SearchResult.LOADING)
                handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
            }
        }
    }

    private fun performSearch() {
        if (searchEditText.text.isEmpty()) return

        hideKeyboard()
        showState(SearchResult.LOADING)

        progressStartTime = System.currentTimeMillis()

        tracksInteractor.searchTracks(
            searchEditText.text.toString(),
            object : TracksInteractor.TrackConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post {
                        val elapsedTime = System.currentTimeMillis() - progressStartTime
                        val remainingTime = PROGRESS_MIN_DISPLAY_TIME - elapsedTime

                        if (remainingTime > 0) {
                            handler.postDelayed({
                                displaySearchResults(foundTracks, errorMessage)
                            }, remainingTime)
                        } else {
                            displaySearchResults(foundTracks, errorMessage)
                        }
                    }
                }
            })
    }

    private fun displaySearchResults(foundTracks: List<Track>?, errorMessage: String?) {
        progressBar.isVisible = false

        if (foundTracks != null) {
            trackList.clear()
            trackList.addAll(foundTracks)
            trackAdapter.notifyDataSetChanged()

            if (trackList.isEmpty()) {
                showState(SearchResult.EMPTY)
            } else {
                showState(SearchResult.CONTENT)
            }
        } else {
            showState(SearchResult.ERROR)
        }
    }

    private fun refreshHistory() {
        historyList.clear()
        historyList.addAll(searchHistoryInteractor.getHistory())
        historyAdapter.notifyDataSetChanged()
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.TRACK_KEY, track)
        }
        startActivity(intent)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private enum class SearchResult { LOADING, CONTENT, EMPTY, ERROR }

    private fun showState(state: SearchResult) {
        progressBar.isVisible = state == SearchResult.LOADING
        recyclerViewTracks.isVisible = state == SearchResult.CONTENT
        placeholderNothingFound.isVisible = state == SearchResult.EMPTY
        placeholderNetworkError.isVisible = state == SearchResult.ERROR

        if (state != SearchResult.CONTENT) {
            historyLayout.isVisible = false
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_search)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top / 2,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }
}