package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity

@Entity(primaryKeys = ["tripId", "seqNum"])
data class DatabaseSourceOrSite(
    var tripId: String,
    var seqNum: Int,
    var wayPointTypeDescription: String,
    var latitude: Double,
    var longitude: Double,
    var destinationCode: String,
    var destinationName: String,
    var siteContainerCode: String? = null,
    var siteContainerDescription: String? = null,
    var address1: String,
    var city: String,
    var stateAbbrev: String,
    var postalCode: Int = 0,
    var delReqNum: Int? = null,
    var delReqLineNum: Int? = null,
    var productId: Int? = null,
    var productCode: String? = null,
    var productDesc: String? = null,
    var requestedQty: Int? = null,
    var uom: String? = null,
    var fill: String = "",
    var trailerId: Int
)