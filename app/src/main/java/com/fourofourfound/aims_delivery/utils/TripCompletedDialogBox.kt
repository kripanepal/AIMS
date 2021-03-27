package com.fourofourfound.aims_delivery.utils

import android.app.AlertDialog
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController

fun getTripCompletedDialogBox(context: Context): AlertDialog.Builder {
    return CustomDialogBuilder(context,
        "Destination Reached",
        "Fill the form now.",
        "Ok",
        null,
        "Cancel",
        null,
        false
    ).builder
}
