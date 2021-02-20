package com.fourofourfound.aims_delivery.database.entities.location

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CustomDatabaseLocation(
    var latitude: String,
    var longitude: String,
    @PrimaryKey var timeStamp: String
)
