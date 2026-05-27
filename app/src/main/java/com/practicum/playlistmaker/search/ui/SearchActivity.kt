package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val viewModel: SearchViewModel by viewModel()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        initViews()
        setupAdapters()
        setupListeners()
        setupWindowInsets()
        setupObservers()

        if (savedInstanceState != null) {
            val savedText = savedInstanceState.getString(SAVE_TEXT, "")
            searchEditText.setText(savedText)
            if (savedText.isNotEmpty()) {
                viewModel.searchDebounce(savedText)
            } else {
                viewModel.restoreStateIfNeeded()
            }
        } else {
            viewModel.restoreStateIfNeeded()
        }

        searchEditText.requestFocus()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVE_TEXT, searchEditText.text.toString())
    }

    override fun onResume() {
        super.onResume()
        if (searchEditText.text.isEmpty()) {
            viewModel.loadHistory()
        }
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
            viewModel.onTrackClick(track)
            openPlayer(track)
        }
        recyclerViewTracks.layoutManager = LinearLayoutManager(this)
        recyclerViewTracks.adapter = trackAdapter

        historyAdapter = TrackAdapter(historyList) { track ->
            viewModel.onTrackClick(track)
            openPlayer(track)
        }
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        recyclerViewHistory.adapter = historyAdapter
    }

    private fun setupListeners() {
        backButton.setOnClickListener { finish() }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            viewModel.onClearButtonClick()
            hideKeyboard()
        }

        refreshButton.setOnClickListener {
            viewModel.searchDebounce(searchEditText.text.toString())
        }

        clearHistoryButton.setOnClickListener {
            viewModel.onClearHistoryClick()
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                viewModel.loadHistory()
            }
        }

        searchEditText.doOnTextChanged { text, _, _, _ ->
            clearButton.isVisible = !text.isNullOrEmpty()
            viewModel.searchDebounce(text.toString())
        }
    }

    private fun setupObservers() {
        viewModel.observeState().observe(this) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: SearchState) {
        progressBar.isVisible = state is SearchState.Loading
        placeholderNothingFound.isVisible = state is SearchState.Empty
        placeholderNetworkError.isVisible = state is SearchState.Error
        recyclerViewTracks.isVisible = state is SearchState.Content
        historyLayout.isVisible = state is SearchState.History && searchEditText.text.isEmpty()

        when (state) {
            is SearchState.Content -> {
                trackList.clear()
                trackList.addAll(state.tracks)
                trackAdapter.notifyDataSetChanged()
            }
            is SearchState.History -> {
                historyList.clear()
                historyList.addAll(state.tracks)
                historyAdapter.notifyDataSetChanged()
            }
            else -> {}
        }
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

    companion object {
        private const val SAVE_TEXT = "SAVE_TEXT"
    }
}