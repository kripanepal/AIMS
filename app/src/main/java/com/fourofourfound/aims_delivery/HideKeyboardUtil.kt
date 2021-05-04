package com.fourofourfound.aims_delivery

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager

/**
 * Hide soft keyboard
 * This method hides the keyboard if user clicks outside the focused view.
 * @param activity the activity where the keyboard needs to be hidden
 */
 fun hideSoftKeyboard(activity: Activity) {
    val inputMethodManager =
        activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
}