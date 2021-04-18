package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity

@Entity(primaryKeys = ["seqNo", "tripId"])
data class DatabaseCompletionForm(
    var billOfLadingNumber: Int?,
    var product: String,
    var startDate: String,
    var startTime: String,
    var endDate: String,
    var endTime: String,
    var grossQty: Int,
    var netQty: Int,
    var trailerBeginReading: Double,
    var trailerEndReading: Double,
    var comments: String,
    var seqNo: Int,
    var tripId: Int,
    var stickReadingBefore: Double?,
    var stickReadingAfter: Double?,
    var meterReadingBefore: Double?,
    var meterReadingAfter: Double?

)