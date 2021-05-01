package com.fourofourfound.aims_delivery.utils

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

fun getRealPathFromURI(context: Context, contentUri: Uri): String {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        cursor.getString(column_index)
    } catch (e: java.lang.Exception) {
        ""
    } finally {
        cursor?.close()
    }
}

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

