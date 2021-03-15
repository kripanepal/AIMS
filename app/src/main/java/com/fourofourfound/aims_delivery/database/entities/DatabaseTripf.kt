package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class DatabaseTripf(
    @PrimaryKey
    var tripId: String,

//    var tripLog: String,
//    var truckID: String,
//    var truckName:  String,
//    var travelID: String,
//    var travelType: String,
    val status: String = "Not Completed"
)
