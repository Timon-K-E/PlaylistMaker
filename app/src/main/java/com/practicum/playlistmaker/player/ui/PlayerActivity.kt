package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.search.domain.Track

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val TRACK_KEY = "TRACK_KEY"
    }

    private lateinit var viewModel: PlayerViewModel

    private lateinit var backButton: Button
    private lateinit var coverImage: ImageView
    private lateinit var songTitle: TextView
    private lateinit var artistName: TextView
    private lateinit var durationValue: TextView
    private lateinit var albumLabel: TextView
    private lateinit var albumValue: TextView
    private lateinit var yearLabel: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreLabel: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryLabel: TextView
    private lateinit var countryValue: TextView
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var currentTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        val track = intent.getParcelableExtra<Track>(TRACK_KEY) ?: run {
            finish()
            return
        }

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return PlayerViewModel(Creator.providePlayerInteractor(), track) as T
            }
        })[PlayerViewModel::class.java]

        initViews()
        applyWindowInsets()
        setupObservers()

        backButton.setOnClickListener {
            viewModel.pausePlayer()
            finish()
        }
        playButton.setOnClickListener { viewModel.playButtonClicked() }
        pauseButton.setOnClickListener { viewModel.pauseButtonClicked() }
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            viewModel.pausePlayer()
        }
    }

    private fun initViews() {
        backButton = findViewById(R.id.back_screen_walkman)
        coverImage = findViewById(R.id.albumCover)
        songTitle = findViewById(R.id.songTitle)
        artistName = findViewById(R.id.artistName)
        durationValue = findViewById(R.id.durationValue)
        albumLabel = findViewById(R.id.albumLabel)
        albumValue = findViewById(R.id.albumValue)
        yearLabel = findViewById(R.id.yearLabel)
        yearValue = findViewById(R.id.yearValue)
        genreLabel = findViewById(R.id.genreLabel)
        genreValue = findViewById(R.id.genreValue)
        countryLabel = findViewById(R.id.countryLabel)
        countryValue = findViewById(R.id.countryValue)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        currentTime = findViewById(R.id.timePlay)
        currentTime.text = "00:00"
    }

    private fun setupObservers() {
        viewModel.observeTrack().observe(this) { track ->
            bindTrackData(track)
        }

        viewModel.observeState().observe(this) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: PlayerState) {
        when (state) {
            is PlayerState.Default -> {
                playButton.isEnabled = true
                playButton.isVisible = true
                pauseButton.isVisible = false
                currentTime.text = "00:00"
            }
            is PlayerState.Prepared -> {
                playButton.isEnabled = true
                playButton.isVisible = true
                pauseButton.isVisible = false
            }
            is PlayerState.Playing -> {
                playButton.isVisible = false
                pauseButton.isVisible = true
            }
            is PlayerState.Paused -> {
                playButton.isVisible = true
                pauseButton.isVisible = false
            }
            is PlayerState.TimeUpdate -> {
                currentTime.text = state.currentTime
            }
            is PlayerState.Completion -> {
                playButton.isEnabled = true
                playButton.isVisible = true
                pauseButton.isVisible = false
                currentTime.text = "00:00"
            }
        }
    }

    private fun bindTrackData(track: Track) {
        songTitle.text = track.trackName
        artistName.text = track.artistName
        durationValue.text = track.formattedTime

        if (track.collectionName.isNullOrEmpty()) {
            albumLabel.isVisible = false
            albumValue.isVisible = false
        } else {
            albumLabel.isVisible = true
            albumValue.isVisible = true
            albumValue.text = track.collectionName
        }

        if (track.releaseDate.isNullOrEmpty()) {
            yearLabel.isVisible = false
            yearValue.isVisible = false
        } else {
            yearLabel.isVisible = true
            yearValue.isVisible = true
            yearValue.text = track.releaseDate.take(4)
        }

        genreValue.text = track.primaryGenreName
        countryValue.text = track.country

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_cover)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.radius_size_8)))
            .into(coverImage)
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_walkman)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top / 2, systemBars.right, systemBars.bottom)
            insets
        }
    }
}