package com.fourofourfound.aims_delivery.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


@RequiresApi(Build.VERSION_CODES.N)
fun bitmapToFile(
    context: Context?,
    bitmap: Bitmap,
    fileNameToSave: String
): File? { // File name like "image.png"
    //create a file to write bitmap data
    var file: File? = null
    return try {
        file =
            File("/sdcard/Pictures/Aims/$fileNameToSave")
        file!!.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos) // YOU can also save it in JPEG
        val bitmapdata: ByteArray = bos.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        file // it will return null
    }
}