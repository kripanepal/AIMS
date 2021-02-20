package com.fourofourfound.aims_delivery.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun checkPermission(permissionsToCheck: List<String>, context: Context): Boolean {
    var toReturn = true
    for (permission in permissionsToCheck) {
        if (ActivityCompat.checkSelfPermission(
                context,
                permission
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            toReturn = false
        }
    }
    return toReturn
}