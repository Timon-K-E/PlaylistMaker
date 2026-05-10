package com.practicum.playlistmaker

import android.media.MediaPlayer
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
import java.text.SimpleDateFormat
import java.util.Locale
import java.time.Instant
import java.time.ZoneId

class WalkmanActivity : AppCompatActivity() {

    companion object {
        private const val DELAY_MILLIS = 200L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private lateinit var backButton: Button
    private lateinit var albumCover: ImageView
    private lateinit var songTitle: TextView
    private lateinit var artistNameView: TextView
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var timePlay: TextView

    private lateinit var durationValue: TextView
    private lateinit var albumValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView
    private lateinit var albumLabel: TextView
    private lateinit var yearLabel: TextView
    private lateinit var genreLabel: TextView
    private lateinit var countryLabel: TextView

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private var currentTrack: Track? = null

    private lateinit var handler: Handler
    private lateinit var updateProgressRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_walkman)

        initViews()

        handler = Handler(Looper.getMainLooper())

        updateProgressRunnable = object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING && mediaPlayer.isPlaying) {
                    val currentPosition = mediaPlayer.currentPosition
                    val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                    timePlay.text = formattedTime
                    handler.postDelayed(this, DELAY_MILLIS)
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_walkman)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top/2, systemBars.right, systemBars.bottom)
            insets
        }

        backButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            playbackControl()
        }

        pauseButton.setOnClickListener {
            playbackControl()
        }

        val track = intent.getParcelableExtra<Track>(IntentKeys.TRACK)

        if (track == null) {
            finish()
            return
        }

        currentTrack = track
        bindTrack(track)
        preparePlayer(track)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::handler.isInitialized) {
            handler.removeCallbacksAndMessages(null)
        }
        mediaPlayer.release()
    }

    private fun initViews() {
        backButton = findViewById(R.id.back_screen_walkman)
        albumCover = findViewById(R.id.albumCover)
        songTitle = findViewById(R.id.songTitle)
        artistNameView = findViewById(R.id.artistName)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        timePlay = findViewById(R.id.timePlay)

        durationValue = findViewById(R.id.durationValue)
        albumValue = findViewById(R.id.albumValue)
        yearValue = findViewById(R.id.yearValue)
        genreValue = findViewById(R.id.genreValue)
        countryValue = findViewById(R.id.countryValue)
        albumLabel = findViewById(R.id.albumLabel)
        yearLabel = findViewById(R.id.yearLabel)
        genreLabel = findViewById(R.id.genreLabel)
        countryLabel = findViewById(R.id.countryLabel)

        timePlay.text = "00:00"
    }

    private fun bindTrack(track: Track) {
        songTitle.text = track.trackName
        artistNameView.text = track.artistName

        val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        durationValue.text = formattedTime

        if (track.collectionName.isNotEmpty()) {
            albumLabel.isVisible = true
            albumValue.isVisible = true
            albumValue.text = track.collectionName
        } else {
            albumLabel.isVisible = false
            albumValue.isVisible = false
        }

        if (track.releaseDate.isNotEmpty()) {
            yearLabel.isVisible = true
            yearValue.isVisible = true
            val year = try {
                Instant.parse(track.releaseDate)
                    .atZone(ZoneId.of("UTC"))
                    .year
                    .toString()
            } catch (e: Exception) {
                ""
            }
            yearValue.text = year
        } else {
            yearLabel.isVisible = false
            yearValue.isVisible = false
        }

        if (track.primaryGenreName.isNotEmpty()) {
            genreLabel.isVisible = true
            genreValue.isVisible = true
            genreValue.text = track.primaryGenreName
        } else {
            genreLabel.isVisible = false
            genreValue.isVisible = false
        }

        if (track.country.isNotEmpty()) {
            countryLabel.isVisible = true
            countryValue.isVisible = true
            countryValue.text = track.country
        } else {
            countryLabel.isVisible = false
            countryValue.isVisible = false
        }

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
            .placeholder(R.drawable.ic_placeholder_cover)
            .centerCrop()
            .transform(RoundedCorners(
                resources.getDimensionPixelSize(R.dimen.radius_size_8)
            ))
            .into(albumCover)
    }

    private fun preparePlayer(track: Track) {
        try {
            val previewUrl = track.previewUrl
            if (previewUrl.isNullOrEmpty()) {
                return
            }

            mediaPlayer.setDataSource(previewUrl)
            mediaPlayer.prepareAsync()

            mediaPlayer.setOnPreparedListener {
                pauseButton.isVisible = false
                playButton.isVisible = true
                playerState = STATE_PREPARED
                timePlay.text = "00:00"
            }

            mediaPlayer.setOnCompletionListener {
                pauseButton.isVisible = false
                playButton.isVisible = true
                playerState = STATE_PREPARED
                timePlay.text = "00:00"
                stopProgressUpdate()
            }

            mediaPlayer.setOnErrorListener { _, _, _ ->
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        pauseButton.isVisible = true
        playButton.isVisible = false
        playerState = STATE_PLAYING
        startProgressUpdate()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        pauseButton.isVisible = false
        playButton.isVisible = true
        playerState = STATE_PAUSED
        stopProgressUpdate()
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startProgressUpdate() {
        handler.post(updateProgressRunnable)
    }

    private fun stopProgressUpdate() {
        handler.removeCallbacks(updateProgressRunnable)
    }
}