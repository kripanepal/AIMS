package com.fourofourfound.aims_delivery.domain

import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.network.location.NetworkLocation


data class CustomLocation(var latitude: String, var longitude: String, var timeStamp: String)

fun CustomLocation.asNetworkModal(): NetworkLocation {
    return NetworkLocation(
        latitude = this.latitude,
        longitude = this.longitude,
        timeStamp = this.timeStamp
    )
}

fun CustomLocation.asDatabaseModal(): CustomDatabaseLocation {
    return CustomDatabaseLocation(
        latitude = this.latitude,
        longitude = this.longitude,
        timeStamp = this.timeStamp
    )
}