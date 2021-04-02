package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import com.fourofourfound.aims_delivery.utils.StatusEnum

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
    var requestedQty: Int = 0,
    var uom: String = "NOT PROVIDED",
    var fill: String = "",

    var status: StatusEnum = StatusEnum.NOT_STARTED
)

