package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseTruck(
    @PrimaryKey
    var truckId: Int,
    var truckCode: String,
    var truckDesc: String,
    val tripId: String
)