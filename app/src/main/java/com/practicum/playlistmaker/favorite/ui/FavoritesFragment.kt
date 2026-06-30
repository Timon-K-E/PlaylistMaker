package com.practicum.playlistmaker.favorite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.favorite.domain.FavoriteState
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModel()

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private var favoriteTracks = mutableListOf<Track>()
    private lateinit var favoriteAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupPlaceholder()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun setupAdapter() {
        favoriteAdapter = TrackAdapter(favoriteTracks) { track ->
            findNavController().navigate(
                R.id.action_libraryFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }

        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesRecyclerView.adapter = favoriteAdapter
    }

    private fun setupPlaceholder() {
        binding.placeholderImage.setImageResource(R.drawable.ic_placeholder_track_error)
        binding.placeholderText.setText(R.string.no_favorites_message)
    }

    private fun renderState(state: FavoriteState) {
        when (state) {
            is FavoriteState.Empty -> {
                binding.placeholderContainer.isVisible = true
                binding.favoritesRecyclerView.isVisible = false
                favoriteTracks.clear()
                favoriteAdapter.notifyDataSetChanged()
            }
            is FavoriteState.Content -> {
                binding.placeholderContainer.isVisible = false
                binding.favoritesRecyclerView.isVisible = true
                favoriteTracks.clear()
                favoriteTracks.addAll(state.tracks)
                favoriteAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        fun newInstance(): FavoritesFragment {
            return FavoritesFragment()
        }
    }

}