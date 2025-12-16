package com.practicum.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class SearchActivity : AppCompatActivity() {

    private var savedSearchText: String = EMPTY_STRING

    companion object {
        const val SAVE_TEXT_KEY = "SAVE_TEXT_KEY"
        const val EMPTY_STRING = ""
    }
    //тут сохраняем текст
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVE_TEXT_KEY, savedSearchText)
    }
    //тут восстаналвиваем текст
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedSearchText = savedInstanceState.getString(SAVE_TEXT_KEY, EMPTY_STRING)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //кнопка назад
        val backDisplayMainActivity = findViewById<Button>(R.id.back_screen_search)
        backDisplayMainActivity.setOnClickListener{
            finish()
        }

        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        val clearButton = findViewById<ImageView>(R.id.clear_button)

        //подставляем в поле данные
        searchEditText.setText(savedSearchText)
        // обновление видимости кнопки
        clearButton.visibility = if (savedSearchText.isEmpty()) View.GONE else View.VISIBLE



        searchEditText.setOnClickListener {
            showKeyboard(searchEditText)
        }

        // Очищаем поле
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard()
            searchEditText.clearFocus()
        }

        //  показываем/скрываем кнопку
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                savedSearchText = s?.toString() ?: ""
            }
        })

        showKeyboard(searchEditText)
    }

    private fun showKeyboard(editText: EditText) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

}