package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import com.fourofourfound.aims_delivery.utils.DeliveryStatusEnum

@Entity(primaryKeys = ["tripId","tripDate"])
data class DatabaseTrip(
    var tripId: Int,
    var tripName: String,
    var tripDate: String,
    var deliveryStatus: DeliveryStatusEnum = DeliveryStatusEnum.NOT_STARTED
)
