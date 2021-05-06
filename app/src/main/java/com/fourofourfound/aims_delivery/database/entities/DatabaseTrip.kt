package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import com.fourofourfound.aims_delivery.utils.DeliveryStatusEnum

/**
 * Database trip
 * This class represents the trip information that is saved in database
 * @property tripId the id of the trip
 * @property tripName the name of the trip
 * @property tripDate the date the trip is started
 * @property deliveryStatus the status of the delivery
 * @constructor Create empty Database trip
 */
@Entity(primaryKeys = ["tripId","tripDate"])
data class DatabaseTrip(
    var tripId: Int,
    var tripName: String,
    var tripDate: String,
    var deliveryStatus: DeliveryStatusEnum = DeliveryStatusEnum.NOT_STARTED
)
