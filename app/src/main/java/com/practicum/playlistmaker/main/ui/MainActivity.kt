package com.practicum.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.ui.LibraryActivity
import com.practicum.playlistmaker.search.ui.SearchActivity
import com.practicum.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        applyWindowInsets()

        val searchButton = findViewById<Button>(R.id.search)
        val libraryButton = findViewById<Button>(R.id.library)
        val settingsButton = findViewById<Button>(R.id.tools)

        searchButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        libraryButton.setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
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