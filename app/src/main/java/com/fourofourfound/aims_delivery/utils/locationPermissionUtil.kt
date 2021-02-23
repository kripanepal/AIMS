package com.fourofourfound.aims_delivery.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat.requestPermissions


class LocationPermissionUtil(var context:Context)
{
    var permissionsToCheck = mutableListOf<String>(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
    )


    fun takeToPermissionScreen():Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri: Uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        return intent
    }

    fun askSDKBasedLocationPermission()
    {
        if (!checkPermission(
                permissionsToCheck,
                context
            )
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requestPermissions(
                    context as Activity,
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    ), 50
                )
            }
            if (Build.VERSION.SDK_INT === 23) {
                requestPermissions( context as Activity,
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    ), 50
                )
            } else {
                requestPermissions( context as Activity,permissionsToCheck.toTypedArray(), 50)
            }
        }
    }


}


