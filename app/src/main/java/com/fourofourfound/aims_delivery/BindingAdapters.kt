package com.fourofourfound.aims_delivery
import android.widget.TextView
import androidx.databinding.BindingAdapter

//sets the error field
@BindingAdapter("errorText")
fun setErrorMessage(view: TextView, errorMessage: String?) {
    view.error = errorMessage
}