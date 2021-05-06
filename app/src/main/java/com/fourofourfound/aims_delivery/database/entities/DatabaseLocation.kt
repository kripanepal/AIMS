package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity

/**
 * Database location
 * THis class represents the information about the location of the destination that is saved in database
 * @property address1 the street address of the destination
 * @property address2 the address of the destination
 * @property city the destination city
 * @property stateAbbrev the destination state
 * @property postalCode the postal code of the destination
 * @property latitude the latitude of the destination
 * @property longitude the longitude of the destination
 * @property destinationCode the code of the destination
 * @property destinationName the name of the destination
 * @property identifier the key that identifies the destination
 * @constructor Create empty Database location
 */
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