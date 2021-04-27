package com.fourofourfound.aims_delivery.utils

import android.view.Gravity
import android.view.View
import android.widget.ImageView
import com.fourofourfound.aimsdelivery.R

fun toggleViewVisibility(view: View) {
    if (view.visibility == View.VISIBLE) {
        animateViewVisibility(view.rootView, view, false, Gravity.TOP)
        view.visibility = View.GONE
    } else {
        animateViewVisibility(view.rootView, view, true, Gravity.TOP)
        view.visibility = View.VISIBLE
    }
}


fun  toggleDropDownImage(parent: View, dropDown: ImageView) {
    if (parent.visibility == View.VISIBLE) {
        dropDown.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
    } else {
        dropDown.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
    }
}


