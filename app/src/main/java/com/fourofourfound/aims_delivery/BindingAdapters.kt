package com.fourofourfound.aims_delivery

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter


@BindingAdapter("visibilityBasedOnStatus")
fun setVisibility(view: View, deliveryStatus: String) {
    if (deliveryStatus == "COMPLETED") {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}

@BindingAdapter("textBasedOnStatus")
fun setStatusBasedText(view: TextView, deliveryStatus: String) {
    if (deliveryStatus == "ONGOING") {
        view.text = "Continue"
    } else {
        view.text = "Show Details"
    }
}