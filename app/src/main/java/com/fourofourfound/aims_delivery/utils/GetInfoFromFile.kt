package com.fourofourfound.aims_delivery.utils

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

/**
 * Get real path from URI
 * This method gets the absolute path of the given URI
 * @param context the context of the application
 * @param contentUri the URI whose path is to be found
 * @return the absolute path of the URI
 */
fun getRealPathFromURI(context: Context, contentUri: Uri): String {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        cursor.getString(columnIndex)
    } catch (e: java.lang.Exception) {
        ""
    } finally {
        cursor?.close()
    }
}

/**
 * Get bit map from file path
 * This method gets the bitmap of the image from the file location
 * @param parentContext the context of the parent
 * @param item the path of the image file
 * @return the bitmap of the image if available
 */
fun getBitMapFromFilePath(
    parentContext: Context,
    item: String
): Bitmap? {
    return try {
        val photoURI = FileProvider.getUriForFile(
            parentContext,
            "com.fourofourfound.aims_delivery",
            File(item)
        )
        val source =
            ImageDecoder.createSource(parentContext.contentResolver, photoURI)
        ImageDecoder.decodeBitmap(source)
    } catch (E: Exception) {
        null
    }
}

