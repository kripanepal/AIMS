package com.fourofourfound.aims_delivery.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * Take to Permission Screen Intent
 * This method takes the user to the permission settings screen
 *
 * @return permission setting intent
 */
fun takeToPermissionScreenIntent(context: Context): Intent {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val uri: Uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    return (intent)
}