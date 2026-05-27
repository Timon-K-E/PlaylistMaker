package com.practicum.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.SharingRepository

class SharingRepositoryImpl(
    private val context: Context
) : SharingRepository {

    override fun shareApp() {
        val shareText = context.getString(R.string.share_app_text)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_app)))
    }

    override fun openSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_subject))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.support_body))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(emailIntent)
    }

    override fun openUserAgreement() {
        val url = context.getString(R.string.user_agreement_url)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(browserIntent)
    }
}