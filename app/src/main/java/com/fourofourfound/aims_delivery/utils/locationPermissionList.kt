package com.fourofourfound.aims_delivery.utils

/**
 * Get location permissions to be checked
 * This method returns the list of permission to be checked
 * @return the list of permission to be checked
 */
fun getLocationPermissionsToBeChecked(): MutableList<String> {
    return mutableListOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
}
