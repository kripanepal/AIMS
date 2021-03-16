package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import com.fourofourfound.aims_delivery.domain.Coordinates
import com.fourofourfound.aims_delivery.domain.Location

@Entity(primaryKeys = ["latitude", "longitude", "sourceOrSiteId"])
data class DatabaseLocationWithAddress(
    var street: String,
    var city: String,
    var state: String,
    var zip: Int,
    @Embedded var gps: DatabaseCoordinates,
    var sourceOrSiteId: String
)


data class DatabaseCoordinates(var latitude: Double, var longitude: Double)

fun DatabaseLocationWithAddress.asDomainModel(): Location {
    return Location(
        street,
        city,
        state,
        zip,
        Coordinates(gps.latitude, gps.longitude)
    )
}