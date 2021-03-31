package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity

@Entity(primaryKeys = ["tripId", "truckId"])
data class TripTruckCrossRef(
    val tripId: Int,
    val truckId: Int
)