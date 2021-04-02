package com.fourofourfound.aims_delivery.database.utilClasses

import androidx.room.TypeConverter
import com.fourofourfound.aims_delivery.utils.StatusEnum

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
}
