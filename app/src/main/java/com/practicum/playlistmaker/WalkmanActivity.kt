package com.practicum.playlistmaker

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale
import java.time.Instant
import java.time.ZoneId

class WalkmanActivity : AppCompatActivity(){

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_walkman)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_walkman)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top/2, systemBars.right, systemBars.bottom)
                insets
            }

            val displaySearchActivity = findViewById<Button>(R.id.back_screen_walkman)
            displaySearchActivity.setOnClickListener{
                finish()
            }

            val trackName = intent.getStringExtra("trackName") ?: ""
            val artistName = intent.getStringExtra("artistName") ?: ""
            val trackTimeMillis = intent.getLongExtra("trackTimeMillis", 0L)
            val artworkUrl100 = intent.getStringExtra("artworkUrl100") ?: ""
            val collectionName = intent.getStringExtra("collectionName") ?: ""
            val releaseDate = intent.getStringExtra("releaseDate") ?: ""
            val country = intent.getStringExtra("country") ?: ""
            val primaryGenreName = intent.getStringExtra("primaryGenreName") ?: ""

            val albumCover = findViewById<ImageView>(R.id.albumCover)
            val songTitle = findViewById<TextView>(R.id.songTitle)
            val artistNameView = findViewById<TextView>(R.id.artistName)
            val timePlay = findViewById<TextView>(R.id.timePlay)
            val durationValue = findViewById<TextView>(R.id.durationValue)
            val albumValue = findViewById<TextView>(R.id.albumValue)
            val yearValue = findViewById<TextView>(R.id.yearValue)
            val genreValue = findViewById<TextView>(R.id.genreValue)
            val countryValue = findViewById<TextView>(R.id.countryValue)

            val albumLabel = findViewById<TextView>(R.id.albumLabel)
            val yearLabel = findViewById<TextView>(R.id.yearLabel)


            songTitle.text = trackName
            artistNameView.text = artistName


            val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
            timePlay.text = formattedTime
            durationValue.text = formattedTime

            setupMetadataField(albumLabel, albumValue, collectionName, "album")
            setupMetadataField(yearLabel, yearValue, releaseDate, "year")



            albumValue.text = collectionName

            if (releaseDate.isNotEmpty()) {
                val year = Instant.parse(releaseDate)
                    .atZone(ZoneId.of("UTC"))
                    .year
                    .toString()
                yearValue.text = year
            } else {
                yearValue.text = ""
            }
            genreValue.text = primaryGenreName
            countryValue.text = country

            Glide.with(this)
                .load(artworkUrl100.replaceAfterLast("/", "512x512bb.jpg"))
                .placeholder(R.drawable.ic_placeholder_cover)
                .centerCrop()
                .transform(RoundedCorners(
                    resources.getDimensionPixelSize(
                        R.dimen.radius_size_8)
                )
                )
                .into(albumCover)

        }
    private fun setupMetadataField(
        labelView: TextView,
        valueView: TextView,
        value: String,
        fieldName: String
    ) {
        if (value.isNotEmpty()) {

            labelView.visibility = TextView.VISIBLE
            valueView.visibility = TextView.VISIBLE
        } else {

            labelView.visibility = TextView.GONE
            valueView.visibility = TextView.GONE
        }
    }
}