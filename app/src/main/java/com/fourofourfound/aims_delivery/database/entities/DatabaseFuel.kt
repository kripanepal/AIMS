package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import com.fourofourfound.aims_delivery.domain.Fuel
import com.fourofourfound.aims_delivery.domain.Quantity

@Entity(primaryKeys = ["type", "sourceOrSiteId"])
class DatabaseFuel(
    var type: String,
    @Embedded var quantity: DatabaseFuelQuantity,
    var sourceOrSiteId: String
)

data class DatabaseFuelQuantity(var volume: Int, var measure: String)

fun DatabaseFuel.asDomainModel(): Fuel {
    return Fuel(type, Quantity(quantity.volume, quantity.measure))
}