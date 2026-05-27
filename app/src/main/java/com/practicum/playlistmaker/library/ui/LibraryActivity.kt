package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.R

class LibraryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_library)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main_library)
        ) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top / 2,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        findViewById<Button>(R.id.back_screen_library)
            .setOnClickListener {
                finish()
            }
    }
}