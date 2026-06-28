package com.practicum.playlistmaker.library.newplaylist.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding

class NewPlaylistFragment : Fragment(R.layout.fragment_new_playlist) {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentNewPlaylistBinding.bind(view)

        //hideSystemBars()

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Кнопка "Создать" (пока отключена)
        binding.createButton.setOnClickListener {
            // Здесь будет логика создания плейлиста
            // Позже добавим
        }
    }

    private fun hideSystemBars() {
        val window = requireActivity().window
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)

        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}