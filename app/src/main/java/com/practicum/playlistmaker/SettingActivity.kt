package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_setting)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val displayMainActivity = findViewById<Button>(R.id.back_setting)

        displayMainActivity.setOnClickListener{
            finish()
        }

        val shareButton = findViewById<TextView>(R.id.share)

        shareButton.setOnClickListener{
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            }
            val chooser = Intent.createChooser(shareIntent, getString(R.string.share_app))
            startActivity(chooser)

        }




        val writeToSupportButtom = findViewById<TextView>(R.id.write_support)


        writeToSupportButtom.setOnClickListener{
            val message = getString(R.string.support_body)
            val email = getString(R.string.support_email)
            val subject = getString(R.string.support_subject)

            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }

        val userAgreementButton = findViewById<TextView>(R.id.user_agreement)

        userAgreementButton.setOnClickListener{
            val url = getString(R.string.user_agreement_url)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        }
    }
}