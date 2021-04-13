package com.fourofourfound.aims_delivery.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import com.fourofourfound.aimsdelivery.R

/**
 * Start call
 *Start an intent to start the call to a
 * specific number
 */
private fun startCall(context: Context) {
    val intent = Intent(Intent.ACTION_DIAL)
    val phoneNumber = "tel:" + context.getString(R.string.provider_number)
    intent.data = Uri.parse(phoneNumber)
    context.startActivity(intent)

}
/**
 * Show dialog
 *method to display the dialog with provider's number
 */
fun showStartCallDialog(context: Context) {
    val dialogView = LayoutInflater.from(context).inflate(
        R.layout.contact_my_provider_dialog, null
    )
    CustomDialogBuilder(
        context,
        "Contact Info",
        null,
        "Call now",
        { startCall(context) },
        "Cancel",
        null,
        false
    ).builder.setView(dialogView).show()
}