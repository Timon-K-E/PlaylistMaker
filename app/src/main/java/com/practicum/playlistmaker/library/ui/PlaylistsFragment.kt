package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.library.domain.PlaylistsState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPlaceholder()
        setupRecyclerView()

        playlistAdapter = PlaylistAdapter(emptyList())
        binding.rvPlaylists.adapter = playlistAdapter

        binding.buttonNewPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_libraryFragment_to_newPlaylistFragment
            )
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.updatePlaylists(playlists)
        }
    }

    private fun setupRecyclerView() {
        val spanCount = 2
        val spacing = resources.getDimensionPixelSize(R.dimen.radius_size_8)
        val edgeMargin = resources.getDimensionPixelSize(R.dimen.element_margin_16)

        binding.rvPlaylists.layoutManager = GridLayoutManager(requireContext(), spanCount)

        binding.rvPlaylists.addItemDecoration(object : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: android.graphics.Rect,
                view: View,
                parent: androidx.recyclerview.widget.RecyclerView,
                state: androidx.recyclerview.widget.RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                val column = position % spanCount
                val totalItems = parent.adapter?.itemCount ?: 0
                val isLastRow = position >= totalItems - spanCount

                when (column) {
                    0 -> {
                        outRect.left = edgeMargin
                        outRect.right = spacing / 2
                    }
                    1 -> {
                        outRect.left = spacing / 2
                        outRect.right = edgeMargin
                    }
                }

                if (position < spanCount) {
                    outRect.top = edgeMargin
                    outRect.bottom = spacing
                } else {
                    outRect.top = spacing
                    outRect.bottom = if (isLastRow) 0 else spacing / 2
                }
            }
        })
    }

    private fun setupPlaceholder() {
        binding.placeholderImage.setImageResource(R.drawable.ic_placeholder_track_error)
        binding.placeholderText.setText(R.string.no_playlists_message)
    }

    private fun renderState(state: PlaylistsState) {
        when (state) {
            PlaylistsState.Empty -> {
                binding.placeholderContainer.isVisible = true
                binding.rvPlaylists.isVisible = false
            }
            PlaylistsState.Content -> {
                binding.placeholderContainer.isVisible = false
                binding.rvPlaylists.isVisible = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }
    }
}