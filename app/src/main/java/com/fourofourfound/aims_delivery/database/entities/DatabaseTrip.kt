package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fourofourfound.aims_delivery.utils.StatusEnum

@Entity(primaryKeys = ["tripId","tripDate"])
data class DatabaseTrip(
    var tripId: Int,
    var tripName: String,
    var tripDate: String,
    var status: StatusEnum = StatusEnum.NOT_STARTED
)
