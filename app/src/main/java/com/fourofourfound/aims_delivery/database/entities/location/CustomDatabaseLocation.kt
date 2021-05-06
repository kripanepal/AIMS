package com.fourofourfound.aims_delivery.database.entities.location

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Custom database location
 * This class represents the location information to be sent to the dispatcher
 * @property latitude the latitude of the user
 * @property longitude the longitude of the user
 * @property timeStamp the time stamp information was collected
 * @constructor Create empty Custom database location
 */
@Entity
data class CustomDatabaseLocation(
    var latitude: Double,
    var longitude: Double,
    @PrimaryKey var timeStamp: String
)
