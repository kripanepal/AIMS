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
        var siteContainerCode: String?,

        @SerializedName("SiteContainerDescription")
        var siteContainerDescription: String?,

        @SerializedName("DelReqNum")
        var delReqNum: Int?,

        @SerializedName("DelReqLineNum")
        var delReqLineNum: Int?,

        @SerializedName("ProductId")
        var productId: Int,

        @SerializedName("ProductCode")
        var productCode: String?,

        @SerializedName("ProductDesc")
        var productDesc: String?,

        @SerializedName("RequestedQty")
        var requestedQty: Int,

        @SerializedName("UOM")
        var uom: String,

        @SerializedName("Fill")
        var fill: String,

        @SerializedName("DriverCode")
        var driverCode: String = "",

        @SerializedName("DriverName")
        var driverName: String = "",
    )



