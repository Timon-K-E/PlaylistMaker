package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.widget.doOnTextChanged
import android.view.inputmethod.EditorInfo
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.os.Handler
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.gson.Gson
import android.widget.ProgressBar


class SearchActivity : AppCompatActivity() {

    private var savedSearchText: String = EMPTY_STRING

    private val trackList = mutableListOf<Track>()

    private lateinit var searchHistory: SearchHistory
    private lateinit var historyAdapter: TrackAdapter
    private var historyList = mutableListOf<Track>()

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter

    private lateinit var placeholderNothingFound: LinearLayout
    private lateinit var placeholderNetworkError: LinearLayout
    private lateinit var refreshButton: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var historyLayout: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button

    private var isClickAllowed = true
    private val searchRunnable = Runnable{ executeSearchQuery()}
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val SAVE_TEXT_KEY = "SAVE_TEXT_KEY"
        private const val EMPTY_STRING = ""
        private const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val SEARCH_DEBOUNCE_DELAY =  2000L
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVE_TEXT_KEY, savedSearchText)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        initializeViews()
        setupSystemInsets()

        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs, Gson())

        restoreSavedState(savedInstanceState)
        setupRecyclerView()
        setupViewListeners()
        if (searchHistory.read().isNotEmpty()) {
            updateHistoryList()
            showHistory()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }


    private fun initializeViews() {
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        recyclerView = findViewById(R.id.recycler_view_track)
        placeholderNothingFound = findViewById(R.id.placeholder_nothing_found)
        placeholderNetworkError = findViewById(R.id.placeholder_network_error)
        refreshButton = findViewById(R.id.refresh_button)
        progressBar = findViewById(R.id.progress_bar)

        historyLayout = findViewById(R.id.search_history)
        historyRecyclerView = findViewById(R.id.recycler_view_history)
        clearHistoryButton = findViewById(R.id.clear_button_history)
    }

     private fun setupRecyclerView() {
         trackAdapter = TrackAdapter(trackList) { track ->
             trackClickDebounce{
                 searchHistory.add(track)
                 openPlayerScreen(track)
             }

         }
         recyclerView.adapter = trackAdapter

         historyAdapter = TrackAdapter(historyList) { track ->
             trackClickDebounce {
                 searchHistory.add(track)
                 openPlayerScreen(track)
                 updateHistoryList()
             }
         }
         historyRecyclerView.adapter = historyAdapter

     }

    private fun openPlayerScreen(track: Track) {
        val intent = Intent(this, WalkmanActivity::class.java).apply {
            putExtra(IntentKeys.TRACK, track)
        }
        startActivity(intent)
    }


    private fun setupSystemInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_search)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top/2,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }

    private fun restoreSavedState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            savedSearchText = savedInstanceState.getString(SAVE_TEXT_KEY, EMPTY_STRING)
            searchEditText.setText(savedSearchText)
        }

        clearButton.visibility = if (savedSearchText.isEmpty()) View.GONE else View.VISIBLE
    }


    private fun setupViewListeners() {
        setupBackButtonListener()
        setupClearButtonListener()
        setupTextChangeListeners()
        setupRefreshButtonListener()
        setupClearHistoryButtonListener()

        setupTextInputListeners()
    }
//4
    private fun setupTextInputListeners() {
        searchEditText.setOnClickListener {
            if (searchEditText.text.isEmpty() && searchHistory.read().isNotEmpty()) {
                updateHistoryList()
                showHistory()
            }
        }
    }

    private fun setupBackButtonListener() {
        val backButton = findViewById<Button>(R.id.back_screen_search)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupClearButtonListener() {
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            trackList.clear()
            trackAdapter.notifyDataSetChanged()
            dismissSoftKeyboard()
            searchEditText.clearFocus()
            handler.removeCallbacks(searchRunnable)
            updateUIState(SearchResult.NO_RESULTS_OR_CLEAR)
        }
    }

    private fun setupClearHistoryButtonListener() {
        clearHistoryButton.setOnClickListener {
            searchHistory.clear()
            historyList.clear()
            historyAdapter.notifyDataSetChanged()
            historyLayout.isVisible = false
        }
    }


    private fun setupTextChangeListeners() {
        searchEditText.doOnTextChanged { text, _, _, _ ->
            clearButton.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
            savedSearchText = text?.toString() ?: ""
            handler.removeCallbacks(searchRunnable)
            if (text.isNullOrEmpty()) {
                hideLoading()
                trackList.clear()
                trackAdapter.notifyDataSetChanged()
                if (searchHistory.read().isNotEmpty()) {
                    updateHistoryList()
                    showHistory()
                } else {
                    hideHistory()
                }
                updateUIState(SearchResult.NO_RESULTS_OR_CLEAR)
            } else {
                hideHistory()
                showLoading()
                handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)

            }
        }


        searchEditText.setOnClickListener {
            showSoftKeyboardFor(searchEditText)
        }
    }


    private fun setupRefreshButtonListener() {
        refreshButton.setOnClickListener {
            executeSearchQuery()
        }
    }


    private fun showSoftKeyboardFor(editText: EditText) {
        editText.requestFocus()
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun dismissSoftKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun executeSearchQuery() {
        dismissSoftKeyboard()
        val searchText = searchEditText.text.toString()
        if (searchText.isEmpty()) {
            updateUIState(SearchResult.NO_RESULTS_OR_CLEAR)
            return
        }

        showLoading()

        ITunesClient.itunesApiService.search(searchText)
            .enqueue(object : Callback<ITunesResponse> {
                override fun onResponse(
                    call: Call<ITunesResponse>,
                    response: Response<ITunesResponse>
                ) {
                    hideLoading()
                    handleSearchResponse(response)
                }
                override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                    hideLoading()
                    updateUIState(SearchResult.ERROR)
                }
            })
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        placeholderNetworkError.visibility = View.GONE
        placeholderNothingFound.visibility = View.GONE
        historyLayout.visibility = View.GONE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    private fun handleSearchResponse(response: Response<ITunesResponse>) {
        // Проверка успешности HTTP запроса (коды 200-299)
        if (response.isSuccessful) {
            trackList.clear()// Очистка предыдущих результатов
            val searchResults = response.body()?.results // Получение результатов из ответа

            if (searchResults?.isNotEmpty() == true) {
                trackList.addAll(searchResults)  // УСПЕХ: Добавление найденных треков в список
                trackAdapter.notifyDataSetChanged()
                updateUIState(SearchResult.SUCCESS)
            } else {
                updateUIState(SearchResult.EMPTY)// УСПЕХ: Сервер ответил, но результатов нет
            }
        } else {
            updateUIState(SearchResult.ERROR)// ОШИБКА: Сервер вернул код ошибки (не 200-299)
        }
    }

    enum class SearchResult {
        SUCCESS,
        EMPTY,
        ERROR,
        NO_RESULTS_OR_CLEAR
    }

    private fun updateUIState(result: SearchResult) {
        hideAllUIStates()
        when (result) {
            SearchResult.SUCCESS -> {
                displaySearchResults()
            }

            SearchResult.EMPTY -> {
                displayEmptyState()
            }

            SearchResult.ERROR -> {
                displayNetworkErrorState()
            }

            SearchResult.NO_RESULTS_OR_CLEAR -> {
                resetSearchUI()
                if (searchEditText.text.isEmpty() && searchHistory.read().isNotEmpty()) {
                    updateHistoryList()
                    showHistory()
                } else {
                    hideHistory()
                }
            }
        }
    }


    private fun hideAllUIStates() {
         recyclerView.isVisible = false
         placeholderNothingFound.isVisible = false
         placeholderNetworkError.isVisible = false
    }


    private fun displaySearchResults() {
         recyclerView.isVisible = true
    }

    private fun displayEmptyState() {
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
        placeholderNothingFound.isVisible = true
    }

    private fun displayNetworkErrorState() {
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
         placeholderNetworkError.isVisible = true
    }

    private fun resetSearchUI() {
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
    }

    private fun updateHistoryList() {
        historyList.clear()
        historyList.addAll(searchHistory.read())
        historyAdapter.notifyDataSetChanged()
    }

    private fun showHistory() {
        historyLayout.isVisible = true
        recyclerView.isVisible = false
        placeholderNothingFound.isVisible = false
        placeholderNetworkError.isVisible = false
    }

    private fun hideHistory() {
        historyLayout.isVisible = false
    }

    private fun trackClickDebounce(action: () -> Unit) {
        if (isClickAllowed) {
            isClickAllowed = false
            action.invoke()
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
    }

}

