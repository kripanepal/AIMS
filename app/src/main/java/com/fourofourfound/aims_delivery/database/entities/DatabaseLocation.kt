package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity

@Entity(primaryKeys = ["latitude", "longitude", "identifier"])
data class DatabaseLocation(
    var address1: String,
    var address2: String?,
    val city: String,
    val stateAbbrev: String,
    val postalCode: Int,
    val latitude: Double,
    val longitude: Double,
    val destinationCode: String,
    val destinationName: String,
    val identifier:String
)