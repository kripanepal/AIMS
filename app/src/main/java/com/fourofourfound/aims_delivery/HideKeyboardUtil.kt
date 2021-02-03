package com.fourofourfound.aims_delivery

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager

 fun hideSoftKeyboard(activity: Activity) {
    val inputMethodManager =
        activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
}