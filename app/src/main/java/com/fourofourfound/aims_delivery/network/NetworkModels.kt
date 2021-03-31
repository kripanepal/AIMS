package com.fourofourfound.aims_delivery.network

import com.google.gson.annotations.SerializedName
    data class NetworkTrip(
        @SerializedName("TruckId")
        var truckId: Int,

        @SerializedName("TruckCode")
        var truckCode: String,

        @SerializedName("TruckDesc")
        var truckDesc: String,

        @SerializedName("TrailerId")
        var trailerId: Int,

        @SerializedName("TrailerCode")
        var trailerCode: String,

        @SerializedName("TrailerDesc")
        var trailerDesc: String,

        @SerializedName("TripId")
        var tripId: Int,

        @SerializedName("TripName")
        var tripName: String,

        @SerializedName("TripDate")
        var tripDate: String,

        @SerializedName("SeqNum")
        var seqNum: Int,

        @SerializedName("WaypointTypeDescription")
        var waypointTypeDescription: String,

        @SerializedName("Latitude")
        var latitude: Double,

        @SerializedName("Longitude")
        var longitude: Double,

        @SerializedName("DestinationCode")
        var destinationCode: String,

        @SerializedName("DestinationName")
        var destinationName: String,

        @SerializedName("Address1")
        var address1: String,

        @SerializedName("Address2")
        var address2: String?,

        @SerializedName("City")
        var city: String,

        @SerializedName("StateAbbrev")
        var stateAbbrev: String,

        @SerializedName("PostalCode")
        var postalCode: Int,

        @SerializedName("SiteContainerCode")
        var siteContainerCode: String? = null,

        @SerializedName("SiteContainerDescription")
        var siteContainerDescription: String? = null,

        @SerializedName("DelReqNum")
        var delReqNum: Int? = null,

        @SerializedName("DelReqLineNum")
        var delReqLineNum: Int? = null,

        @SerializedName("ProductId")
        var productId: Int = 0,

        @SerializedName("ProductCode")
        var productCode: String = "NOT PROVIDED",

        @SerializedName("ProductDesc")
        var productDesc: String = "NOT PROVIDED",

        @SerializedName("RequestedQty")
        var requestedQty: Int = 0,

        @SerializedName("UOM")
        var uom: String = "NOT PROVIDED",

        @SerializedName("Fill")
        var fill: String = "",
    )



