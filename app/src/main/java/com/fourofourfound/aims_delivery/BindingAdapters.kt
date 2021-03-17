package com.fourofourfound.aims_delivery

import android.view.View
import androidx.databinding.BindingAdapter


@BindingAdapter("visibilityBasedOnStatus")
fun setVisibility(view: View, deliveryStatus: String) {
    if (deliveryStatus == "COMPLETED") {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}