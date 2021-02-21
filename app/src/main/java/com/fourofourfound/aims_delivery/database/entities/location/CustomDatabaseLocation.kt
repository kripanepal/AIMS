package com.fourofourfound.aims_delivery.database.entities.location

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CustomDatabaseLocation(
    var latitude: Double,
    var longitude: Double,
    @PrimaryKey var timeStamp: String
)
