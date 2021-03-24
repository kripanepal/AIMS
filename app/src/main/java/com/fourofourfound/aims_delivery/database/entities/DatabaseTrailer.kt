package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity

@Entity(primaryKeys = ["trailerId", "truckId"])
class DatabaseTrailer(

    var trailerId: Int,
    var trailerCode: String,
    var trailerDesc: String,
    var truckId: Int
)

