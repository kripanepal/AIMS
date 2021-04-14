package com.fourofourfound.aims_delivery.utils

import android.view.View
import java.text.SimpleDateFormat
import java.util.*

fun toggleViewVisibility(view: View) {
    view.visibility = if (view.visibility == View.VISIBLE) View.GONE else View.VISIBLE
}

fun Date.toString1(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}