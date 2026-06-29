package com.practicum.playlistmaker.utils

import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R

fun Fragment.showCustomToast(message: String, duration: Int = Toast.LENGTH_LONG) {
    val inflater = LayoutInflater.from(requireContext())
    val layout = inflater.inflate(R.layout.custom_toast_layout, null)

    val textView = layout.findViewById<TextView>(R.id.tv_toast_message)
    textView.text = message

    val toast = Toast(requireContext())
    toast.duration = duration
    toast.view = layout

    val offsetPx = (0 * resources.displayMetrics.density).toInt()
    toast.setGravity(Gravity.BOTTOM, 0, offsetPx)

    toast.show()
}