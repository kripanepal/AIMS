package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity

@Entity(primaryKeys = ["tripId", "seqNum"])
data class DatabaseSourceOrSite(
    var tripId: Int,
    var truckId: Int,
    var trailerId: Int,
    var productId: Int,
    var destinationCode: String,
    var seqNum: Int,
    var wayPointTypeDescription: String,

    var siteContainerCode: String? = null,
    var siteContainerDescription: String? = null,
    var delReqNum: Int? = null,
    var delReqLineNum: Int? = null,
    var requestedQty: Int? = null,
    var uom: String? = null,
    var fill: String? = "",

    var status: String = "NOT_STARTED"
)

