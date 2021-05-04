package com.fourofourfound.aims_delivery.utils

import android.app.Activity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Hide action bar
 * This method hides the action bar in the application
 * @param activity the activity whose action bar is to be hidden
 */
fun hideActionBar(activity: Activity) {
    (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
}

/**
 * Show Action Bar
 * This method shows the action bar in the application
 * @param activity the activity whose action bar is to be shown
 */
fun showActionBar(activity: Activity) {
    (activity as AppCompatActivity?)!!.supportActionBar!!.show()
}

/**
 * Hide Bottom Navigation
 * This method hides the bottom navigation bar in the application
 * @param activity the activity whose bottom navigation bar is to be hidden
 */
fun hideBottomNavigation(activity: Activity) {
    activity.bottom_navigation.visibility = View.GONE
}

/**
 * Show Bottom Navigation
 * This method shows the bottom navigation bar in the application
 * @param activity the activity whose bottom navigation bar is to be shown
 */
fun showBottomNavigation(activity: Activity) {
    activity.bottom_navigation.visibility = View.VISIBLE
}