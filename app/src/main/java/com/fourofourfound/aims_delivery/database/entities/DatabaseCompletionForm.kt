package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import java.util.*

/**
 * Database completion form
 * This class represent the form to be filled after the delivery is completed that is saved in database
 * @property driverId the id of the driver
 * @property sourceOrSiteId the id of the destination
 * @property billOfLadingNumber the bill of lading number
 * @property productRequested the requested product
 * @property productDelivered the delivered product
 * @property startTime the start time of the delivery
 * @property endTime the end time of the delivery
 * @property grossQty the gross quantity of the fuel
 * @property netQty the net quantity of the fuel
 * @property trailerBeginReading the trailer reading before filling
 * @property trailerEndReading the trailer reading after filling
 * @property comments the comments for that trip
 * @property seqNo the sequence number of the destination
 * @property tripId the id of trip
 * @property stickReadingBefore the stick reading before filling
 * @property stickReadingAfter the stick reading after filling
 * @property meterReadingBefore the meter reading before filling
 * @property meterReadingAfter the meter reading after filling
 * @property wayPointType the destination type
 * @property identifier the key that identifies the destination
 * @property dataSent the flag that represents if the data is sent or not
 * @constructor Create empty Database completion form
 */
@Entity(primaryKeys = ["seqNo", "tripId"])
data class DatabaseCompletionForm(
    var driverId: String,
    var sourceOrSiteId: Int,
    var billOfLadingNumber: String?,
    var productRequested: Int,
    var productDelivered: Int,
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
    var wayPointType: String,
    var identifier: String = "$tripId $seqNo",
    var dataSent: Boolean = false
)