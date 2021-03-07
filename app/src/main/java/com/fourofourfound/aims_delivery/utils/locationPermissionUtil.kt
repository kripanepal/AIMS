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

/**
 * Background location permission util
 * This class is responsible for checking and managing background location
 * @property context current state of the application
 * @constructor Create empty Background location permission util
 */
class BackgroundLocationPermissionUtil(var context: Context) {

    lateinit var permissionMissingDialog: AlertDialog
    private var permissionsToCheck = getLocationPermissionsToBeChecked()

    init {
        showLocationPermissionMissingDialog()
    }

    /**
     * Take to Permission Screen Intent
     * This method takes the user to the permission settings screen
     *
     * @return permission setting intent
     */
    private fun takeToPermissionScreenIntent(): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri: Uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        return (intent)
    }

    /**
     * On permission selected
     * This method is called everytime the permission for the
     * application is changed
     */
    fun onPermissionSelected() {
        if (!permissionMissingDialog.isShowing) {
            if (!checkPermission(permissionsToCheck, context as MainActivity)
            ) {
                showLocationPermissionMissingDialog()
                //permissionMissingDialog.show()
            }
        }
    }

    /**
     * Check Permission On Start
     * This method shows the permission missing dialog on the startup
     * if the permission is not granted.
     *
     */
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

    /**
     * Show Location Permission Missing Dialog
     * This method creates a location permission missing dialog box.
     */
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







