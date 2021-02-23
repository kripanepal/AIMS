package com.fourofourfound.aims_delivery.utils

import android.os.Build

fun getPermissionsToBeChecked(): MutableList<String> {
    return if (Build.VERSION.SDK_INT >= 29)
        mutableListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    else {
        mutableListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,

            )
    }
}
