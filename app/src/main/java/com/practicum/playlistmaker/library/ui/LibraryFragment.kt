package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment(R.layout.fragment_library) {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private lateinit var pagerAdapter: LibraryPagerAdapter
    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentLibraryBinding.bind(view)
        setupTabs()

    }

    override fun onDestroyView() {
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        super.onDestroyView()
        _binding = null
    }


    private fun setupTabs() {
        pagerAdapter = LibraryPagerAdapter(requireActivity())
        binding.viewPager.adapter = pagerAdapter

        tabLayoutMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.favorites_tab)
                else -> getString(R.string.playlists_tab)
            }
        }
        tabLayoutMediator?.attach()
    }
}