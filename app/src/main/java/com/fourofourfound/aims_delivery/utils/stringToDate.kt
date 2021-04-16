package com.fourofourfound.aims_delivery.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun stringToDate(date: String): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US)
    val outputFormat = SimpleDateFormat("EEE dd-MMM-yyyy hh:mm a", Locale.US)
    return parseDate(date, inputFormat, outputFormat)

}

private fun parseDate(
    inputDateString: String,
    inputDateFormat: SimpleDateFormat,
    outputDateFormat: SimpleDateFormat
): String? {
    var date: Date? = null
    var outputDateString: String? = null
    try {
        date = inputDateFormat.parse(inputDateString)
        outputDateString = outputDateFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return outputDateString
}