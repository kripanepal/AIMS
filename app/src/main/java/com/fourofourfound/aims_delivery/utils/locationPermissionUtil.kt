package com.fourofourfound.aims_delivery.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat.requestPermissions
import com.fourofourfound.aims_delivery.MainActivity


class BackgroundLocationPermissionUtil(var context: Context) {

    lateinit var permissionMissingDialog: AlertDialog
    var permissionsToCheck = mutableListOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    init {
        if (Build.VERSION.SDK_INT >= 29) {
            permissionsToCheck.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }


    private fun takeToPermissionScreenIntent(): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri: Uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        return (intent)
    }

    fun onPermissionSelected() {
        if (!permissionMissingDialog.isShowing) {
            if (!checkPermission(permissionsToCheck, context as MainActivity)
            ) {
                showLocationPermissionMissingDialog()
                permissionMissingDialog.show()
            }
        }
    }

    fun checkPermissionsOnStart() {
        val tempPermissionList = permissionsToCheck.toMutableList()
        if (!checkPermission(permissionsToCheck, context as MainActivity)) {
            if (Build.VERSION.SDK_INT >= 30) {
                tempPermissionList.removeLast()
            }
            requestPermissions(context as Activity, tempPermissionList.toTypedArray(), 50)
            showLocationPermissionMissingDialog()
        }

    }

    private fun showLocationPermissionMissingDialog() {
        permissionMissingDialog = CustomDialogBuilder(
            context,
            "Missing background location access",
            "Please provide background location access all the time. " +
                    "This app uses background location to track the delivery",
            "Enable location",
            { context.startActivity(takeToPermissionScreenIntent()) },
            null,
            null,
            false
        ).builder.create()
    }

}







