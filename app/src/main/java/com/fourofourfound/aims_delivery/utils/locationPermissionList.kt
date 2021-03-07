package com.fourofourfound.aims_delivery.utils

import android.os.Build

/**
 * Get location permissions to be checked
 * This method returns the list of permission to be checked
 * depending on the android API version.
 * @return the list of permission to be checked
 */
fun getLocationPermissionsToBeChecked(): MutableList<String> {
    return if (Build.VERSION.SDK_INT >= 29)
        mutableListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    else {
        mutableListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,

            )
    }
}
