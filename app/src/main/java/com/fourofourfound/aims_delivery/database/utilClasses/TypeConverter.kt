package com.fourofourfound.aims_delivery.database.utilClasses

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.fourofourfound.aims_delivery.utils.StatusEnum
import java.io.ByteArrayOutputStream

// example converter for java.util.Date
class StatusConverter {
    @TypeConverter
    fun fromIntToStatusEnum(value: Int): StatusEnum {
        return when (value) {
            0 -> StatusEnum.ONGOING
            1 -> StatusEnum.NOT_STARTED
            else -> StatusEnum.COMPLETED
        }
    }

    @TypeConverter
    fun fromStatusEnumToInt(value: StatusEnum): Int {
        return when (value) {
            StatusEnum.ONGOING -> 0
            StatusEnum.NOT_STARTED -> 1
            else -> 2
        }
    }

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }


}
