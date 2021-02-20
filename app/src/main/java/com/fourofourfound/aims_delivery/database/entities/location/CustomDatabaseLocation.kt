package com.fourofourfound.aims_delivery.database.entities.location

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fourofourfound.aims_delivery.network.location.NetworkLocation

@Entity
data class CustomDatabaseLocation(
    var latitude: String,
    var longitude: String,
    @PrimaryKey var timeStamp: String
)

fun CustomDatabaseLocation.asNetworkModal(): NetworkLocation {
    return NetworkLocation(
        latitude = this.latitude,
        longitude = this.longitude,
        timeStamp = this.timeStamp
    )
}