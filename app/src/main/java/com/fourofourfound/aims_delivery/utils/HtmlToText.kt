package com.fourofourfound.aims_delivery.utils

import android.text.Spanned
import androidx.core.text.HtmlCompat

/**
 * Html to text
 * This method converts html to formatted text
 * @param string the string that needs to be formatted
 * @return the formatted string
 */
fun htmlToText(string: String): Spanned {
    return HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_COMPACT)
}