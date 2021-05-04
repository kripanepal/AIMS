package com.fourofourfound.aims_delivery.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Format date
 * This method formats given date and time in the defined pattern
 * @param date the date that needs to be formatted
 * @return formatted date and time
 */
fun formatDate(date: String): String? {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US)
    val outputFormat = SimpleDateFormat("EEE dd-MMM-yyyy hh:mm a", Locale.US)
    return parseDate(date, inputFormat, outputFormat)
}

/**
 * Parse date
 * This method formats the input date string to the desired date string
 * @param inputDateString the date and time that needs to be formatted
 * @param inputDateFormat the pattern for date and time in input date
 * @param outputDateFormat the pattern for date and time in output date
 * @return
 */
private fun parseDate(
    inputDateString: String,
    inputDateFormat: SimpleDateFormat,
    outputDateFormat: SimpleDateFormat
): String? {
    var date: Date?
    var outputDateString: String? = null
    try {
        date = inputDateFormat.parse(inputDateString)
        outputDateString = outputDateFormat.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return outputDateString
}

/**
 * Get time
 * This method provides the current time in the defined pattern
 * @param calendar calendar object to get the date and time from
 * @return the current time in HH:mm:ss
 */
fun getTime(calendar: Calendar): String {
    return SimpleDateFormat("HH:mm:ss", Locale.US).format(calendar.time)
}

/**
 * Get date
 * This method provides the current date in the defined pattern
 * @param calendar calendar object to get the date and time from
 * @return the current date in HH:mm:ss
 */
fun getDate(calendar: Calendar): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
}

/**
 * Get date and time
 * This method combines the date and time in defined format
 * @param calendar calendar class to get the date and time from
 * @return formatted date and time
 */
fun getDateAndTime(calendar: Calendar): String {
    return "${getDate(calendar)} ${getTime(calendar)}"
}