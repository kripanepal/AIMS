package com.fourofourfound.aims_delivery

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.utils.formatDate
import com.fourofourfound.aimsdelivery.R
import com.here.android.mpa.odml.MapPackage
import java.text.SimpleDateFormat
import java.util.*

/**
 * Set visibility for map state
 *
 * @param view
 * @param mapPackage
 */
@BindingAdapter("imageBasedOnStatus")
fun setVisibilityForMapState(view: ImageView, mapPackage: MapPackage) {
    view.setImageResource( if (mapPackage.installationState == MapPackage.InstallationState.INSTALLED) R.drawable.delete_icn else R.drawable.download_icn)
}

@BindingAdapter("setIntAsString")
fun setTextAsString(view: TextView, int: Int) {
    view.text = int.toString()
}

@BindingAdapter("setCalendarTime")
fun setCalendarTime(view: TextView, cal: Calendar) {
    view.text = SimpleDateFormat("HH:mm",Locale.US).format(cal.time)
}

@BindingAdapter("setCalendarDate")
fun setCalendarDate(view: TextView, cal: Calendar) {
    view.text = SimpleDateFormat("yyyy:MM:dd",Locale.US).format(cal.time)
}

@BindingAdapter("android:text")
fun setText(view: TextView, value: Int?) {
   view.text = value?.toString() ?: "-1"
}

@InverseBindingAdapter(attribute = "android:text")
fun getText(view: TextView): Int {
    val value = view.text.toString()
    if (!isInteger(value)) return -1
    return value.toInt()
}

fun isInteger(str: String?) = str?.toIntOrNull()?.let { true } ?: false

@BindingAdapter("visibilityBasedOnDestination")
fun visibilityBasedOnDestination(view: View, sourceOrSite: SourceOrSite) {
    view.visibility =
        if (sourceOrSite.wayPointTypeDescription == "Source") View.GONE else View.VISIBLE
}

@BindingAdapter("android:text")
fun setTextValue(view: TextView, value: Double?) {
    view.text = value?.toString() ?: ""
}

@InverseBindingAdapter(attribute = "android:text")
fun getTextValue(view: TextView): Double? {
    val value = view.text.toString()
    return  if (!isDouble(value)) null else value.toDouble()
}

fun isDouble(str: String?) = str?.toDoubleOrNull()?.let { true } ?: false


@BindingAdapter("stringToDate")
fun stringToDateAdapter(view: TextView, value: String?) {
    val converted = value?.let { formatDate(it) }
    view.text = if (converted.isNullOrEmpty()) "" else converted
}