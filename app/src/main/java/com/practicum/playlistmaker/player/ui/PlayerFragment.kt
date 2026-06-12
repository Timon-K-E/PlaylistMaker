package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.PlayerState
import com.practicum.playlistmaker.search.domain.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding

class PlayerFragment : Fragment(R.layout.fragment_player) {

    private val track: Track by lazy {
        requireArguments().getParcelable<Track>(TRACK_KEY)
            ?: throw IllegalStateException("Track not found")
    }
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(track)
    }

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentPlayerBinding.bind(view)
        setupObservers()

        binding.backScreenWalkman.setOnClickListener { findNavController().navigateUp() }
        binding.playButton.setOnClickListener { viewModel.playButtonClicked() }
        binding.pauseButton.setOnClickListener { viewModel.pauseButtonClicked() }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.isPlaying()) {
            binding.playButton.isVisible = false
            binding.pauseButton.isVisible = true
            binding.timePlay.text = viewModel.getCurrentPosition()
        } else if (viewModel.getCurrentPositionMillis() > 0) {
            binding.playButton.isVisible = true
            binding.pauseButton.isVisible = false
            binding.timePlay.text = viewModel.getCurrentPosition()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        if (!requireActivity().isChangingConfigurations) {
            viewModel.pausePlayer()
        }
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        if (!requireActivity().isChangingConfigurations) {
            viewModel.pausePlayer()
        }
    }

    private fun setupObservers() {
        viewModel.observeTrack().observe(viewLifecycleOwner) { track ->
            bindTrackData(track)
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: PlayerState) {
        when (state) {
            is PlayerState.Default -> {
                binding.playButton.isEnabled = true
                binding.playButton.isVisible = true
                binding.pauseButton.isVisible = false
                binding.timePlay.text = viewModel.zeroTimeString
            }

            is PlayerState.Prepared -> {
                binding.playButton.isEnabled = true
                binding.playButton.isVisible = true
                binding.pauseButton.isVisible = false
            }

            is PlayerState.Playing -> {
                binding.playButton.isVisible = false
                binding.pauseButton.isVisible = true
            }

            is PlayerState.Paused -> {
                binding.playButton.isVisible = true
                binding.pauseButton.isVisible = false
            }

            is PlayerState.TimeUpdate -> {
                binding.timePlay.text = state.currentTime
            }

            is PlayerState.Completion -> {
                binding.playButton.isEnabled = true
                binding.playButton.isVisible = true
                binding.pauseButton.isVisible = false
                binding.timePlay.text = viewModel.zeroTimeString
            }
        }
    }

    private fun bindTrackData(track: Track) {
        binding.songTitle.text = track.trackName
        binding.artistName.text = track.artistName
        binding.durationValue.text = track.formattedTime

        if (track.collectionName.isNullOrEmpty()) {
            binding.albumLabel.isVisible = false
            binding.albumValue.isVisible = false
        } else {
            binding.albumLabel.isVisible = true
            binding.albumValue.isVisible = true
            binding.albumValue.text = track.collectionName
        }

        if (track.releaseDate.isNullOrEmpty()) {
            binding.yearLabel.isVisible = false
            binding.yearValue.isVisible = false
        } else {
            binding.yearLabel.isVisible = true
            binding.yearValue.isVisible = true
            binding.yearValue.text = track.releaseDate.take(4)
        }

        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_cover)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.radius_size_8)))
            .into(binding.albumCover)
    }

    companion object {
        const val TRACK_KEY = "TRACK_KEY"
        fun createArgs(track: Track): Bundle =
            bundleOf(TRACK_KEY to track)
    }
}