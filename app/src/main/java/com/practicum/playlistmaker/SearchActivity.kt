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
import android.widget.Toast
import com.google.gson.Gson


class SearchActivity : AppCompatActivity() {

    // СОСТОЯНИЕ И ДАННЫЕ
    // Сохраненный текст поиска (для восстановления при повороте экрана)
    private var savedSearchText: String = EMPTY_STRING

    // Список треков для отображения в RecyclerView
    private val trackList = mutableListOf<Track>()
    //  История поиска
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyAdapter: TrackAdapter
    private var historyList = mutableListOf<Track>()

    // UI КОМПОНЕНТЫ
    // Основные элементы интерфейса
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter

    // Плейсхолдеры для разных состояний экрана
    private lateinit var placeholderNothingFound: LinearLayout
    private lateinit var placeholderNetworkError: LinearLayout
    private lateinit var refreshButton: Button

    // Элементы истории
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button

    //  КОНСТАНТЫ
    companion object {
        private const val SAVE_TEXT_KEY = "SAVE_TEXT_KEY"
        private const val EMPTY_STRING = ""
        private const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
    }

    // ЖИЗНЕННЫЙ ЦИКЛ
    // Сохранение состояния при повороте экрана
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVE_TEXT_KEY, savedSearchText)
    }

    // Основной метод инициализации Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        initializeViews()// 1. Инициализация всех View элементов
        setupSystemInsets()// 2. Настройка отступов для системных панелей

        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs, Gson())

        restoreSavedState(savedInstanceState)// 3. Восстановление состояния при повороте экрана
        setupRecyclerView()// 4. Настройка адаптера для RecyclerView
        setupViewListeners()// 5. Настройка всех слушателей событий
        showSoftKeyboardFor(searchEditText)// 6. Показать клавиатуру при открытии экрана
    }

    //  ИНИЦИАЛИЗАЦИЯ КОМПОНЕНТОВ

    // Поиск и инициализация всех View элементов из макета
    private fun initializeViews() {
        searchEditText = findViewById(R.id.search_edit_text)// Поле ввода для поиска
        clearButton = findViewById(R.id.clear_button)// Кнопка очистки поля поиска
        recyclerView = findViewById(R.id.recycler_view_track) // RecyclerView для отображения списка треков
        placeholderNothingFound = findViewById(R.id.placeholder_nothing_found)// Плейсхолдер "Ничего не найдено"
        placeholderNetworkError = findViewById(R.id.placeholder_network_error)// Плейсхолдер "Ошибка сети"
        refreshButton = findViewById(R.id.refresh_button)// Кнопка "Обновить" при ошибке сети

        historyLayout = findViewById(R.id.search_history)
        historyRecyclerView = findViewById(R.id.recycler_view_history)
        clearHistoryButton = findViewById(R.id.clear_button_history)
    }

     // Настройка адаптера для RecyclerView
     private fun setupRecyclerView() {
         // обработчик клика для добавления в историю
         trackAdapter = TrackAdapter(trackList) { track ->
             searchHistory.add(track) // Добавляем трек в историю
             Toast.makeText(
                 this, "Трек добавлен в историю", Toast.LENGTH_SHORT
             ).show()
         }
         recyclerView.adapter = trackAdapter

         // Инициализация адаптера истории
         historyAdapter = TrackAdapter(historyList) { track ->
             // При клике на трек в истории также добавляем его в историю (обновляем позицию)
             searchHistory.add(track)
             // Обновляем список истории после добавления
             updateHistoryList()
         }
         historyRecyclerView.adapter = historyAdapter

     }

    // Настройка отступов для системных панелей (статусной и навигационной)
    private fun setupSystemInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_search)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }

     // Восстановление сохраненного состояния
    private fun restoreSavedState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            savedSearchText = savedInstanceState.getString(SAVE_TEXT_KEY, EMPTY_STRING)
            searchEditText.setText(savedSearchText)
        }

        // Обновление видимости кнопки очистки на основе сохраненного текста
        clearButton.visibility = if (savedSearchText.isEmpty()) View.GONE else View.VISIBLE
    }

    //  НАСТРОЙКА СЛУШАТЕЛЕЙ СОБЫТИЙ
    // Настройка всех слушателей событий для UI элементов
    private fun setupViewListeners() {
        setupBackButtonListener()
        setupClearButtonListener()
        setupTextChangeListeners()
        setupKeyboardDoneListener()
        setupRefreshButtonListener()
        setupClearHistoryButtonListener()
    }

    // Настройка слушателя для кнопки "Назад"
    private fun setupBackButtonListener() {
        val backButton = findViewById<Button>(R.id.back_screen_search)
        backButton.setOnClickListener {
            finish() // Закрытие текущей Activity
        }
    }

     // Настройка слушателя для кнопки очистки поля поиска
    private fun setupClearButtonListener() {
        clearButton.setOnClickListener {
            searchEditText.text.clear()// Очистка поля ввода
            trackList.clear()// Очистка списка треков
            trackAdapter.notifyDataSetChanged()
            dismissSoftKeyboard()// Скрытие клавиатуры
            searchEditText.clearFocus()// Снятие фокуса с поля ввода
            updateUIState(SearchResult.NO_RESULTS_OR_CLEAR)// Показать состояние "поле очищено"
        }
    }

    private fun setupClearHistoryButtonListener() {
        clearHistoryButton.setOnClickListener {
            searchHistory.clear() // Очищаем историю в SharedPreferences
            historyList.clear() // Очищаем локальный список
            historyAdapter.notifyDataSetChanged() // Обновляем адаптер
            historyLayout.visibility = View.GONE // Скрываем блок истории
        }
    }


    // Настройка слушателей изменения текста в поле поиска
    private fun setupTextChangeListeners() {
        searchEditText.doOnTextChanged { text, _, _, _ ->
            // Обновление видимости кнопки очистки
            clearButton.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
            // Сохранение текста для восстановления состояния
            savedSearchText = text?.toString() ?: ""

            //  Логика показа истории при пустом поле
            if (text.isNullOrEmpty()) {
                // Если поле пустое и есть история - показываем историю
                if (searchHistory.read().isNotEmpty()) {
                    updateHistoryList() // Обновляем список истории
                    showHistory()
                } else {
                    hideHistory() // Скрываем историю, если она пустая
                }
                updateUIState(SearchResult.NO_RESULTS_OR_CLEAR)
            } else {
                hideHistory() // Если есть текст - скрываем историю
            }
        }

        // Показ истории при фокусе на поле ввода
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty() && searchHistory.read().isNotEmpty()) {
                updateHistoryList() // Обновляем список истории
                showHistory() // Показываем историю
            }
        }

        // Показ клавиатуры при клике на поле ввода
        searchEditText.setOnClickListener {
            showSoftKeyboardFor(searchEditText)
        }
    }

    // Настройка обработки нажатия "Done" на клавиатуре
    private fun setupKeyboardDoneListener() {
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchEditText.text.isNotEmpty()) {
                    executeSearchQuery() // Выполнить поиск
                } else {
                    dismissSoftKeyboard()
                    updateUIState(SearchResult.NO_RESULTS_OR_CLEAR)
                }
                true
            } else {
                false
            }
        }
    }

     // Настройка слушателя для кнопки "Обновить" при ошибке сети
    private fun setupRefreshButtonListener() {
        refreshButton.setOnClickListener {
            executeSearchQuery() // Повторный запрос поиска
        }
    }

    // УПРАВЛЕНИЕ КЛАВИАТУРОЙ
    // Показать клавиатуру для указанного EditText
    private fun showSoftKeyboardFor(editText: EditText) {
        editText.requestFocus()
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

     // Скрыть клавиатуру
    private fun dismissSoftKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    //  ЛОГИКА ПОИСКА
    // Выполнение поискового запроса
    private fun executeSearchQuery() {
        dismissSoftKeyboard()// Скрываем клавиатуру для удобства просмотра результатов
        val searchText = searchEditText.text.toString()
        // Проверка на пустой запрос
        if (searchText.isEmpty()) {
            updateUIState(SearchResult.NO_RESULTS_OR_CLEAR)
            return
        }
        // Выполнение сетевого запроса через Retrofit
        ITunesClient.itunesApiService.search(searchText)
            .enqueue(object : Callback<ITunesResponse> {
                override fun onResponse(
                    call: Call<ITunesResponse>,
                    response: Response<ITunesResponse>
                ) {
                    handleSearchResponse(response)
                }
                override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {

                    updateUIState(SearchResult.ERROR)// Ошибка сети (нет подключения и др.)
                }
            })
    }

     // Обработка ответа от сервера поиска
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

    // ========== УПРАВЛЕНИЕ СОСТОЯНИЯМИ ЭКРАНА ==========
    // Перечисление возможных состояний экрана поиска
    enum class SearchResult {
        SUCCESS,
        EMPTY,
        ERROR,
        NO_RESULTS_OR_CLEAR
    }

    // Управление видимостью UI элементов в зависимости от состояния
    private fun updateUIState(result: SearchResult) {
        hideAllUIStates() // Сначала скрываем все возможные состояния
        // Включаем только нужные элементы для текущего состояния
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
                // При очистке проверяем, нужно ли показать историю
                if (searchEditText.text.isEmpty() && searchHistory.read().isNotEmpty()) {
                    updateHistoryList() // Обновляем список истории
                    showHistory() // Показываем историю
                } else {
                    hideHistory() // Скрываем историю
                }
            }
        }
    }

     // Скрытие всех UI элементов состояний
    private fun hideAllUIStates() {
        recyclerView.visibility = View.GONE
        placeholderNothingFound.visibility = View.GONE
        placeholderNetworkError.visibility = View.GONE
    }

     // Показать список с результатами поиска
    private fun displaySearchResults() {
        recyclerView.visibility = View.VISIBLE
    }

    // Показать состояние "Ничего не найдено"
    private fun displayEmptyState() {
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
        placeholderNothingFound.visibility = View.VISIBLE
    }

     // Показать состояние "Ошибка сети"
    private fun displayNetworkErrorState() {
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
        placeholderNetworkError.visibility = View.VISIBLE
    }

     // Очистить результаты поиска
    private fun resetSearchUI() {
        trackList.clear()
        trackAdapter.notifyDataSetChanged()
    }

    // Обновление списка истории из SharedPreferences
    private fun updateHistoryList() {
        historyList.clear()
        historyList.addAll(searchHistory.read())
        historyAdapter.notifyDataSetChanged()
    }

    // Показать блок истории
    private fun showHistory() {
        historyLayout.visibility = View.VISIBLE
    }

    // Скрыть блок истории
    private fun hideHistory() {
        historyLayout.visibility = View.GONE
    }
}