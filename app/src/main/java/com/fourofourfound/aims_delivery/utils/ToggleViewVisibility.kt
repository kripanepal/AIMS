package com.fourofourfound.aims_delivery.utils

import android.view.View

fun toggleViewVisibility(view: View) {
    view.visibility = if (view.visibility == View.VISIBLE) View.GONE else View.VISIBLE
}