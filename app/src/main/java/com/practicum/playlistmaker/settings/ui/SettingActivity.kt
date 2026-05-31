package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)

        applyWindowInsets()

        val backButton = findViewById<Button>(R.id.back_setting)
        val themeSwitch = findViewById<SwitchCompat>(R.id.themeSwitcher)
        val shareButton = findViewById<TextView>(R.id.share)
        val supportButton = findViewById<TextView>(R.id.write_support)
        val agreementButton = findViewById<TextView>(R.id.user_agreement)

        viewModel.observeTheme().observe(this) { isDarkTheme ->
            themeSwitch.isChecked = isDarkTheme
        }

        viewModel.observeNavigationCommands().observe(this) { event ->
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
                            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
                            putExtra(Intent.EXTRA_TEXT, getString(R.string.support_body))
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

        themeSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.onThemeChanged(checked)
        }

        backButton.setOnClickListener { finish() }
        shareButton.setOnClickListener { viewModel.shareApp() }
        supportButton.setOnClickListener { viewModel.openSupport() }
        agreementButton.setOnClickListener { viewModel.openUserAgreement() }
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_setting)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top / 2, systemBars.right, systemBars.bottom)
            insets
        }
    }
}