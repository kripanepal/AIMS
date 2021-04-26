package com.fourofourfound.aims_delivery.utils

import android.view.Gravity
import android.view.View

fun toggleViewVisibility(view: View) {
    if (view.visibility == View.VISIBLE) {
        animateViewVisibility(view.rootView, view, false, Gravity.TOP)
        view.visibility = View.GONE
    } else {
        animateViewVisibility(view.rootView, view, true, Gravity.TOP)
        view.visibility = View.VISIBLE
    }
}

