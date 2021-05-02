package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["seqNo", "tripId"])
data class DatabaseCompletionForm(
    var billOfLadingNumber: String?,
    var product: String,
    var startTime: Calendar,
    var endTime: Calendar,
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
    var meterReadingAfter: Double?,
    var identifier: String = "$tripId $seqNo"

)