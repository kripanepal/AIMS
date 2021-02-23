package com.fourofourfound.aims_delivery.utils

import android.app.AlertDialog
import android.content.Context

class CustomDialogBuilder(
    var context: Context,
    var title: String,
    var message: String?,
    var positiveButtonText: String,
    var positiveMessageCallback: (() -> Unit),
    var negativeButtonText: String?,
    var negativeMessageCallback: (() -> Unit)?,
    var cancelable: Boolean
) {

    val builder = AlertDialog.Builder(context)
    init {
        generateDialog()
    }

    private fun generateDialog() {

        builder.setTitle(title)
        message?.apply { builder.setMessage(this) }

        if (positiveButtonText.isNotEmpty())
            builder.setPositiveButton(positiveButtonText) { dialog, which ->
                positiveMessageCallback()
            }
        if (!negativeButtonText.isNullOrEmpty())
            builder.setNegativeButton(negativeButtonText) { dialog, which ->
                negativeMessageCallback?.let { it() }
            }

        builder.setCancelable(cancelable)
    }

}