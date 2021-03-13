package com.fourofourfound.aims_delivery.database.entities.fuel

import androidx.room.Entity

@Entity(primaryKeys = ["type", "id"])
class Fuel(
    var type: String,
    var volume: Int,
    var measure: String,
    var id: String

)