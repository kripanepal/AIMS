package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseTrip(
    @PrimaryKey
    var tripId: String,
    var tripName: String,
    var tripDate: String,
    var status: String = "NOT_STARTED"
)
