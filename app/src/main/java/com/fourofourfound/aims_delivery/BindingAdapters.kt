package com.fourofourfound.aims_delivery

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.fourofourfound.aimsdelivery.R
import com.here.android.mpa.odml.MapPackage


@BindingAdapter("visibilityBasedOnStatus")
fun setVisibility(view: View, deliveryStatus: String) {
    if (deliveryStatus == "COMPLETED") {
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