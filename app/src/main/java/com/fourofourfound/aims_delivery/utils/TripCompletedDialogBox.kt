package com.fourofourfound.aims_delivery.utils

import android.app.AlertDialog
import android.content.Context

fun getTripCompletedDialogBox(context: Context, navigateToForm: () -> Unit): AlertDialog.Builder {
    return CustomDialogBuilder(
        context,
        "Destination Reached",
        "Fill the form now.",
        "Ok",
        navigateToForm,
        "Cancel",
        null,
        false
    ).builder
}
