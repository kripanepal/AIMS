package com.fourofourfound.aims_delivery.utils

import android.app.AlertDialog
import android.content.Context

/**
 * This class creates alert dialog
 *
 * @property context current context of the application
 * @property title the title of the dialog
 * @property message the message for the custom dialog box
 * @property positiveButtonText the text that is displayed on the positive button on the dialog
 * @property positiveMessageCallback the call back that is fired after the positive button is text
 * @property negativeButtonText the text that is displayed on the negative button on the dialog
 * @property negativeMessageCallback the call back that is fired after the negative button is text
 * @property cancelable sets if the dialog box is cancellable or not
 */
class CustomDialogBuilder(
    var context: Context,
    var title: String,
    var message: String?,
    var positiveButtonText: String,
    var positiveMessageCallback: (() -> Unit)?,
    var negativeButtonText: String?,
    var negativeMessageCallback: (() -> Unit)?,
    var cancelable: Boolean
) {

    /**
     * Builder
     * The builder instance that is assigned to the dialog box
     */
    val builder = AlertDialog.Builder(context)

    init {
        generateDialog()
    }

    /**
     * Generate Dialog
     * This method generates a dialog box with a
     * title, message and buttons.
     */
    private fun generateDialog() {
        builder.setTitle(title)
        message?.apply { builder.setMessage(this) }
        if (!positiveButtonText.isNullOrEmpty())
            builder.setPositiveButton(positiveButtonText) { dialog, which ->
                positiveMessageCallback?.let { it() }
            }
        if (!negativeButtonText.isNullOrEmpty())
            builder.setNegativeButton(negativeButtonText) { dialog, which ->
                negativeMessageCallback?.let { it() }
            }
        builder.setCancelable(cancelable)
    }
}