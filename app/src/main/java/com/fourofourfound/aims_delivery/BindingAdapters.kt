package com.fourofourfound.aims_delivery
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("errorText")
fun setErrorMessage(view: TextView, errorMessage: String?) {
    view.error = errorMessage
}