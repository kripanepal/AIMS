package com.fourofourfound.aims_delivery.database.entities.location

import androidx.room.Entity

@Entity(primaryKeys = ["latitude", "longitude"])
class LocationWithAddress(
    var street: String,
    var city: String,
    var state: String,
    var zip: Int,
    var latitude: Double,
    var longitude: Double,
    var id: String
)