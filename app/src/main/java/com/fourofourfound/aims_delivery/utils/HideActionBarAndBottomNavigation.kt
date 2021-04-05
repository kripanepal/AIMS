package com.fourofourfound.aims_delivery.utils

import android.app.Activity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

fun hideActionBar(activity: Activity) {
    (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
}

fun showActionBar(activity: Activity) {
    (activity as AppCompatActivity?)!!.supportActionBar!!.show()
}

fun hideBottomNavigation(activity: Activity) {
    activity.bottom_navigation.visibility = View.GONE

}

fun showBottomNavigation(activity: Activity) {
    activity.bottom_navigation.visibility = View.VISIBLE

}