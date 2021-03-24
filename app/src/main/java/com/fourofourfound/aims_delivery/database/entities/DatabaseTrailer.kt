package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class DatabaseTrailer(
    @PrimaryKey
    var trailerId: Int,
    var trailerCode: String,
    var trailerDesc: String,
    var truckId: Int
)

