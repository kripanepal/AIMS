package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseForm(
    @PrimaryKey(autoGenerate = true)
    var billOfLadingNumber: Int,
    var product: String,
    var startDate: String,
    var startTime: String,
    var endDate: String,
    var endTime: String,
    var grossQty: Int,
    var netQty: Int,
    var trailerBeginReading: Int,
    var trailerEndReading: Int,
    var comments: String
)