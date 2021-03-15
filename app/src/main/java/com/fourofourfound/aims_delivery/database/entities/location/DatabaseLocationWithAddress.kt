package com.fourofourfound.aims_delivery.database.entities.location

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class DatabaseLocationWithAddress(
    var street: String,
    var city: String,
    var state: String,
    var zip: Int,
    @PrimaryKey
    @Embedded var gps: DatabaseCoordinates,
    var sourceOrSiteId: String
)


data class DatabaseCoordinates(var latitude: Double, var longitude: Double)