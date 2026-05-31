package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.FavoritesState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModel()

    private lateinit var placeholderContainer: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeholderContainer = view.findViewById(R.id.placeholder_container)
        placeholderImage = view.findViewById(R.id.placeholder_image)
        placeholderText = view.findViewById(R.id.placeholder_text)

        setupPlaceholder()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
    }

    private fun setupPlaceholder() {
        placeholderImage.setImageResource(R.drawable.ic_placeholder_track_error)
        placeholderText.setText(R.string.no_favorites_message)
    }

    private fun renderState(state: FavoritesState) {
        when (state) {
            FavoritesState.Empty -> {
                placeholderContainer.isVisible = true
            }
            FavoritesState.Content -> {
                placeholderContainer.isVisible = false
            }
        }
    }

    companion object {
        fun newInstance(): FavoritesFragment {
            return FavoritesFragment()
        }
    }

}