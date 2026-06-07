package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.fragment_setting) {
    private val viewModel: SettingsViewModel by viewModel()

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingBinding.bind(view)
        setupObservers()
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        viewModel.observeTheme().observe(viewLifecycleOwner) { isDarkTheme ->
            binding.themeSwitcher.isChecked = isDarkTheme
        }
        viewModel.observeNavigationCommands().observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { command ->
                when (command) {
                    is SettingsNavigationCommand.ShareApp -> {
                        val shareText = getString(R.string.share_app_text)
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        startActivity(shareIntent)
                    }

                    is SettingsNavigationCommand.OpenSupport -> {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(
                                Intent.EXTRA_EMAIL,
                                arrayOf(
                                    getString(R.string.support_email)
                                )
                            )
                            putExtra(
                                Intent.EXTRA_SUBJECT,
                                getString(R.string.support_subject)
                            )
                            putExtra(
                                Intent.EXTRA_TEXT, getString(R.string.support_body)
                            )
                        }
                        startActivity(emailIntent)
                    }

                    is SettingsNavigationCommand.OpenUserAgreement -> {
                        val url = getString(R.string.user_agreement_url)
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(browserIntent)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.onThemeChanged(checked)
        }
        binding.share.setOnClickListener {
            viewModel.shareApp()
        }
        binding.writeSupport.setOnClickListener {
            viewModel.openSupport()
        }
        binding.userAgreement.setOnClickListener {
            viewModel.openUserAgreement()
        }
    }
}