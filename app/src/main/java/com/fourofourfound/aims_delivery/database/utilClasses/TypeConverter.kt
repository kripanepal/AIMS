package com.fourofourfound.aims_delivery.database.utilClasses

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.fourofourfound.aims_delivery.utils.DeliveryStatusEnum
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Status converter
 * This class is used to convert the information to and from the database.
 * @constructor Create empty Status converter
 */
class StatusConverter {

    /**
     * From int to status enum
     * This method converts the number to the status message.
     * @param value the status of the trip
     * @return status of the trip as DeliveryStatusEnum
     */
    @TypeConverter
    fun fromIntToStatusEnum(value: Int): DeliveryStatusEnum {
        return when (value) {
            0 -> DeliveryStatusEnum.ONGOING
            1 -> DeliveryStatusEnum.NOT_STARTED
            else -> DeliveryStatusEnum.COMPLETED
        }
    }

    /**
     * From status enum to int
     * This method converts the number to the status message.
     * @param value the status of the trip as integer
     * @return the status of teh trip as integer
     */
    @TypeConverter
    fun fromStatusEnumToInt(value: DeliveryStatusEnum): Int {
        return when (value) {
            DeliveryStatusEnum.ONGOING -> 0
            DeliveryStatusEnum.NOT_STARTED -> 1
            else -> 2
        }
    }

    /**
     * From bitmap
     * This method converts the bitmap to the byte array.
     * @param bitmap the bitmap of the image
     * @return the byte array of the bitmap
     */
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    /**
     * To bitmap
     * This method converts the image to the bitmap.
     * @param byteArray the array used to represent the bitmap
     * @return the bitmap of the image
     */
    @TypeConverter
    fun toBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    /**
     * From calender
     * This method converts the calender instance to the formatted date and time.
     * @param calendar the calender instance
     * @return the date and time as string
     */
    @TypeConverter
    fun fromCalender(calendar: Calendar): String {
        return SimpleDateFormat("HH:mm:ss/yyyy-MM-dd", Locale.US).format(calendar.time)
    }

    /**
     * String to calender
     * This method converts the date and time string to the calender format.
     * @param string the date and time as a string
     * @return the calender instance
     */
    @TypeConverter
    fun stringToCalender(string: String): Calendar {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("HH:mm:ss/yyyy-MM-dd", Locale.US)
        cal.time = sdf.parse(string)!!
        return cal
    }
}
