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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.playlists.domain.Playlist

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

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var playlistAdapter: PlaylistBottomSheetAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentPlayerBinding.bind(view)
        setupObservers()
        setupBottomSheet()
        setupClickListeners()
        setupRecyclerView()
        loadPlaylists()
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

    private fun setupClickListeners() {
        binding.backScreenWalkman.setOnClickListener { findNavController().navigateUp() }
        binding.playButton.setOnClickListener { viewModel.playButtonClicked() }
        binding.pauseButton.setOnClickListener { viewModel.pauseButtonClicked() }

        binding.addToFavoritesButton.setOnClickListener { viewModel.onFavoriteButtonClicked() }

        binding.addToPlaylistButton.setOnClickListener {
            loadPlaylists()
            toggleBottomSheet()
        }

        binding.overlay.setOnClickListener {
            hideBottomSheet()
        }


        binding.buttonNewPlaylist.setOnClickListener {
            hideBottomSheet()
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height)
        bottomSheetBehavior.skipCollapsed = false

        binding.overlay.isVisible = false

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.overlay.isVisible = true
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.overlay.isVisible = true
                    }
                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
    }

    private fun setupRecyclerView() {
        binding.playlistRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        playlistAdapter = PlaylistBottomSheetAdapter(emptyList())

        // Обработчик клика по плейлисту
        playlistAdapter.setOnItemClickListener { playlist ->
            // TODO: Добавить трек в плейлист
            // viewModel.addTrackToPlaylist(track, playlist)
            hideBottomSheet()
        }
        binding.playlistRecyclerView.adapter = playlistAdapter
    }

    private fun loadPlaylists() {
        viewModel.loadPlaylists()
    }

    private fun toggleBottomSheet() {
        when (bottomSheetBehavior.state) {
            BottomSheetBehavior.STATE_EXPANDED -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            BottomSheetBehavior.STATE_COLLAPSED -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            BottomSheetBehavior.STATE_HIDDEN -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            else -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun hideBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.overlay.isVisible = false
    }

    private fun setupObservers() {
        viewModel.observeTrack().observe(viewLifecycleOwner) { track ->
            bindTrackData(track)
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        viewModel.observeFavoriteState().observe(viewLifecycleOwner) { isFavorite ->
            updateFavoriteButton(isFavorite)
        }

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            updatePlaylists(playlists)
        }
    }

    private fun updatePlaylists(playlists: List<Playlist>) {
        playlistAdapter.updateData(playlists)
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        if (isFavorite) {
            binding.addToFavoritesButton.setImageResource(R.drawable.ic_activ_favorite_border_51_51)
        } else {
            binding.addToFavoritesButton.setImageResource(R.drawable.ic_favorite_border_51_51)
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