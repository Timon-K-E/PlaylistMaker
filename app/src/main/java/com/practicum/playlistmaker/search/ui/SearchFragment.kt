package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModel()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val trackList = mutableListOf<Track>()
    private val historyList = mutableListOf<Track>()
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSearchBinding.bind(view)

        setupAdapters()
        setupListeners()
        setupObservers()

        if (savedInstanceState != null) {

            val savedText =
                savedInstanceState.getString(SAVE_TEXT, "")

            binding.searchEditText.setText(savedText)

            if (savedText.isNotEmpty()) {
                viewModel.searchDebounce(savedText)
            } else {
                viewModel.restoreStateIfNeeded()
            }

        } else {

            viewModel.restoreStateIfNeeded()
        }

        binding.searchEditText.requestFocus()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        _binding?.let {
            outState.putString(
                SAVE_TEXT,
                it.searchEditText.text.toString()
            )
        }
    }

    override fun onResume() {
        super.onResume()

        _binding?.let {
            if (it.searchEditText.text.isEmpty()) {
                viewModel.loadHistory()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun setupAdapters() {

        trackAdapter = TrackAdapter(trackList) { track ->
            viewModel.onTrackClick(track)
            openPlayer(track)
        }

        binding.recyclerViewTrack.layoutManager =
            LinearLayoutManager(requireContext())

        binding.recyclerViewTrack.adapter = trackAdapter

        historyAdapter = TrackAdapter(historyList) { track ->
            viewModel.onTrackClick(track)
            openPlayer(track)
        }

        binding.recyclerViewHistory.layoutManager =
            LinearLayoutManager(requireContext())

        binding.recyclerViewHistory.adapter = historyAdapter
    }

    private fun setupListeners() {

        binding.clearButton.setOnClickListener {
            binding.searchEditText.setText("")
            viewModel.onClearButtonClick()
            hideKeyboard()
        }

        binding.refreshButton.setOnClickListener {
            viewModel.searchDebounce(binding.searchEditText.text.toString())
        }

        binding.clearButtonHistory.setOnClickListener {
            viewModel.onClearHistoryClick()
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()) {
                viewModel.loadHistory()
            }
        }

        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            binding.clearButton.isVisible = !text.isNullOrEmpty()
            viewModel.searchDebounce(text.toString())
        }
    }

    private fun setupObservers() {
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: SearchState) {
        val binding = _binding ?: return
        binding.progressBar.isVisible = state is SearchState.Loading
        binding.placeholderNothingFound.isVisible = state is SearchState.Empty
        binding.placeholderNetworkError.isVisible = state is SearchState.Error
        binding.recyclerViewTrack.isVisible = state is SearchState.Content
        binding.searchHistory.isVisible =
            state is SearchState.History && binding.searchEditText.text.isEmpty()

        when (state) {
            is SearchState.Content -> {
                trackList.clear()
                trackList.addAll(state.tracks)
                trackAdapter.notifyDataSetChanged()
            }

            is SearchState.History -> {
                historyList.clear()
                historyList.addAll(state.tracks)
                historyAdapter.notifyDataSetChanged()
            }

            else -> {}
        }
    }

    private fun openPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_searchFragment_to_playerFragment,
            PlayerFragment.createArgs(track)
        )
    }

    private fun hideKeyboard() {
        val binding = _binding ?: return
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }


    companion object {
        private const val SAVE_TEXT = "SAVE_TEXT"
    }
}