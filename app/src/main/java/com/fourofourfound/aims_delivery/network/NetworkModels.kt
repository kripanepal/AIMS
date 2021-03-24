package com.fourofourfound.aims_delivery.network

import com.fourofourfound.aims_delivery.domain.SourceOrSite

class NetworkModels {

    data class NetworkTrip(
        var truckId: Int,
        var truckCode: String,
        var truckDesc: String,
        var trailerId: Int,
        var trailerCode: String,
        var trailerDesc: String,
        var tripId: String,
        var tripName: String,
        var tripDate: String,
        var sourceOrSite: SourceOrSite,
        val status: String = "NOT_STARTED",
    )

    data class NetworkSourceOrSite(
        var seqNum: Int = 0,
        var waypointTypeDescription: String = "",
        var latitude: Double = 0.0,
        var longitude: Double = 0.0,
        var destinationCode: String = "",
        var destinationName: String = "",
        var address1: String = "",
        var city: String = "",
        var stateAbbrev: String = "",
        var postalCode: Int = 0,

        var siteContainerCode: String? = null,
        var siteContainerDescription: String? = null,
        var delReqNum: Int? = null,
        var delReqLineNum: Int? = null,
        var productId: Int? = null,
        var productCode: String? = null,
        var productDesc: String? = null,
        var requestedQty: Int? = null,
        var uom: String? = null,
        var fill: String = "",
    )


}