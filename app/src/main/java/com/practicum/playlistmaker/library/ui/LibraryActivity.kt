package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R

class LibraryActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: LibraryPagerAdapter

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
        setupTabs()
        setupBackButton()

    }

    private fun setupTabs() {
        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)

        pagerAdapter = LibraryPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.favorites_tab)
                else -> getString(R.string.playlists_tab)
            }
        }.attach()
    }

    private fun setupBackButton() {
        findViewById<Button>(R.id.back_screen_library)
            .setOnClickListener {
                finish()
            }
    }
}