package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Embedded
import androidx.room.Entity

@Entity(primaryKeys = ["type", "sourceOrSiteId"])
class Fuel(
    var type: String,
    @Embedded var quantity: Quantity,
    var sourceOrSiteId: String
)

data class Quantity(var volume: Int, var measure: String)