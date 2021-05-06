package com.fourofourfound.aims_delivery.deliveryForms.finalForm

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Open camera
 * This method is responsible for opening the camera intent.
 */
fun BOLFormFragment.openCamera() {
    getImageContent.launch(context?.let { getPickImageIntent(it) })
}

/**
 * Get pick image intent
 * This method is responsible for opening camera or gallery.
 * @param context the current context of the application
 * @return camera or gallery intent
 */
@SuppressLint("RestrictedApi")
fun BOLFormFragment.getPickImageIntent(context: Context): Intent? {
    val builder = VmPolicy.Builder()
    StrictMode.setVmPolicy(builder.build())
    var chooserIntent: Intent? = null
    var intentList: MutableList<Intent> = ArrayList()
    val pickIntent = Intent(
        Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    )
    val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    takePhotoIntent.putExtra("data", true)

    val photoURI = FileProvider.getUriForFile(
        context,
        "com.fourofourfound.aims_delivery",
        createImageFile(context)
    )
    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
    intentList = addIntentsToList(context, intentList, pickIntent)
    intentList = addIntentsToList(context, intentList, takePhotoIntent)
    if (intentList.isNotEmpty()) {
        chooserIntent = Intent.createChooser(
            intentList.removeAt(intentList.size - 1),
            "Capture/select bill of lading picture"
        )
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS,
            intentList.toTypedArray()
        )
    }
    return chooserIntent
}

/**
 * Add intents to list
 * This method added the camera and gallery intent to the list.
 * @param context the current context of the application
 * @param list the list to hold the intent
 * @param intent the intent to be added
 * @return list of added intents
 */
private fun addIntentsToList(
    context: Context,
    list: MutableList<Intent>,
    intent: Intent
): MutableList<Intent> {
    val resInfo: List<ResolveInfo> = context.packageManager.queryIntentActivities(intent, 0)
    for (resolveInfo in resInfo) {
        val packageName = resolveInfo.activityInfo.packageName
        val targetedIntent = Intent(intent)
        targetedIntent.setPackage(packageName)
        list.add(targetedIntent)
    }
    return list
}

/**
 * Create image file
 * This method creates the image file with jpg extension
 * @param context the current context of the application
 * @return the image file
 */
private fun BOLFormFragment.createImageFile(context: Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "${viewModel.tripId} ${viewModel.destination.seqNum}-${timeStamp} - "
    val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    val image = File.createTempFile(
        imageFileName,
        ".jpg",
        storageDir
    )
    currentPhotoPath = image.absolutePath
    return image
}

