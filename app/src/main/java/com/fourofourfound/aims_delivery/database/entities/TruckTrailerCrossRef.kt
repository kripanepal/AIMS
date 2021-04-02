package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity

@Entity(primaryKeys = ["tripId", "truckId"])
data class TruckTrailerCrossRef(
    val tripId: Int,
    val truckId: Int
)