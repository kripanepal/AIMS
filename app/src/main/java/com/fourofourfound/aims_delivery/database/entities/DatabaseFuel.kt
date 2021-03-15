package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Embedded
import androidx.room.Entity

@Entity(primaryKeys = ["type", "sourceOrSiteId"])
class DatabaseFuel(
    var type: String,
    @Embedded var quantity: DatabaseFuelQuantity,
    var sourceOrSiteId: String
)

data class DatabaseFuelQuantity(var volume: Int, var measure: String)