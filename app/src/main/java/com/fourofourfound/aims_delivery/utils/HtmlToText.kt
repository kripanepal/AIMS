package com.fourofourfound.aims_delivery.utils

import android.text.Spanned
import androidx.core.text.HtmlCompat

fun htmlToText(string: String): Spanned {
    return HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_COMPACT)
}