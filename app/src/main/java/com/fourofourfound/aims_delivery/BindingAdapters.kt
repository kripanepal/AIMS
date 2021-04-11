package com.fourofourfound.aims_delivery

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.fourofourfound.aims_delivery.utils.StatusEnum
import com.fourofourfound.aimsdelivery.R
import com.here.android.mpa.odml.MapPackage
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("visibilityBasedOnStatus")
fun setVisibility(view: View, deliveryStatus: StatusEnum) {
    if (deliveryStatus == StatusEnum.COMPLETED) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}

@BindingAdapter("imageBasedOnStatus")
fun setVisibilityForMapState(view: ImageView, mapPackage: MapPackage) {
    if (mapPackage.installationState == MapPackage.InstallationState.INSTALLED)
        view.setImageResource(R.drawable.delete_icn)
    else
        view.setImageResource(R.drawable.download_icn)

}


@BindingAdapter("textBasedOnStatus")
fun setStatusBasedText(view: TextView, deliveryStatus: String) {
    if (deliveryStatus == "ONGOING") {
        view.text = "Continue"
    } else {
        view.text = "Show Details"
    }
}


@BindingAdapter("setIntAsString")
fun setTextAsString(view: TextView, int: Int) {
    view.text = int.toString()
}

@BindingAdapter("setCalendarTime")
fun setCalendarTime(view: TextView, cal: Calendar) {
    view.text = SimpleDateFormat("HH:mm").format(cal.time)
}

@BindingAdapter("setCalendarDate")
fun setCalendarDate(view: TextView, cal: Calendar) {
    view.text = SimpleDateFormat("yyyy:MM:dd").format(cal.time)
}

@BindingAdapter("android:text")
fun setText(view: TextView, value: Int?) {
    if (value == null)
        view.text = "-1"
    else
        view.text = value.toString()

}

@InverseBindingAdapter(attribute = "android:text")
fun getText(view: TextView): Int {
    var value = view.text.toString()
    if (!isInteger(value)) return -1
    return value.toInt()
}

fun isInteger(str: String?) = str?.toIntOrNull()?.let { true } ?: false