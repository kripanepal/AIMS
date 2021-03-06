package com.fourofourfound.aims_delivery.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 * Check Permission
 * This method checks if required permission is granted or not.
 *
 * @param permissionsToCheck List of permissions to be checked
 * @param context current state of the application
 * @return true if all permissions have been granted
 */
fun checkPermission(permissionsToCheck: List<String>, context: Context): Boolean {
    var toReturn = true
    for (permission in permissionsToCheck) {
        if (ActivityCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            toReturn = false
        }
    }
    return toReturn
}