package com.fourofourfound.aims_delivery.utils

import android.app.Activity
import android.view.View
import com.fourofourfound.aims_delivery.shared_view_models.SharedViewModel
import com.fourofourfound.aimsdelivery.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

fun showUserNotClockedInMessage(sharedViewModel:SharedViewModel, view: View, activity: Activity): Boolean {
    if (!sharedViewModel.userClockedIn.value!!) {
        Snackbar.make(
            view,
            "Please clock in to continue",
            Snackbar.LENGTH_LONG
        )
            .setAction("Clock In") {
                activity.bottom_navigation.selectedItemId =
                    R.id.settings_navigation
            }
            .show()
        return true
    }
    return false
}