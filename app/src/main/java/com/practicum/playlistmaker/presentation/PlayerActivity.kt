package com.practicum.playlistmaker.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val TRACK_KEY = "TRACK_KEY"
        private const val UPDATE_DELAY = 200L
    }

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

    private lateinit var playerInteractor: PlayerInteractor
    private var mainThreadHandler: Handler? = null
    private var timerRunnable: Runnable? = null


    private lateinit var currentTrack: Track


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        initViews()
        applyWindowInsets()
        playerInteractor = Creator.providePlayerInteractor()
        mainThreadHandler = Handler(Looper.getMainLooper())


        currentTrack = intent.getParcelableExtra(TRACK_KEY) ?: run {
            finish()
            return
        }

        bindTrackData(currentTrack)
        preparePlayer(currentTrack.previewUrl)

        backButton.setOnClickListener { finish() }
        playButton.setOnClickListener { startPlayer() }
        pauseButton.setOnClickListener { pausePlayer() }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.releasePlayer()
        timerRunnable?.let { mainThreadHandler?.removeCallbacks(it) }
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

    private fun preparePlayer(url: String) {
        playButton.isEnabled = false

        playerInteractor.preparePlayer(url, object : PlayerInteractor.PlayerPreparedListener {
            override fun onPrepared() {
                playButton.isEnabled = true
                playButton.isVisible = true
                pauseButton.isVisible = false
            }
        })

        playerInteractor.setOnCompletionListener {
            playButton.isVisible = true
            pauseButton.isVisible = false
            currentTime.text = "00:00"
            timerRunnable?.let { mainThreadHandler?.removeCallbacks(it) }
        }
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        playButton.isVisible = false
        pauseButton.isVisible = true
        startTimer()
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer()
        playButton.isVisible = true
        pauseButton.isVisible = false
        timerRunnable?.let { mainThreadHandler?.removeCallbacks(it) }
    }

    private fun startTimer() {
        timerRunnable = object : Runnable {
            override fun run() {
                if (playerInteractor.isPlaying()) {
                    val currentPosition = playerInteractor.getCurrentPosition()
                    currentTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                    mainThreadHandler?.postDelayed(this, UPDATE_DELAY)
                } else {
                    stopTimer()
                }
            }
        }
        mainThreadHandler?.post(timerRunnable!!)
    }

    private fun stopTimer(){
        timerRunnable?.let{mainThreadHandler?.removeCallbacks(it)}
    }
    private fun applyWindowInsets() {

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_walkman)) { view, insets ->
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