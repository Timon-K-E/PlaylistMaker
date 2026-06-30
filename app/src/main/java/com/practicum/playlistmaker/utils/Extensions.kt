package com.practicum.playlistmaker.utils

import android.content.Context
import kotlin.math.roundToInt

fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).roundToInt()
}