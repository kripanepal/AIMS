package com.fourofourfound.aims_delivery.network

import com.google.gson.annotations.SerializedName

class NetworkModels {

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
        var tripId: String,

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
        var productId: Int? = null,

        @SerializedName("ProductCode")
        var productCode: String? = null,

        @SerializedName("ProductDesc")
        var productDesc: String? = null,

        @SerializedName("RequestedQty")
        var requestedQty: Int? = null,

        @SerializedName("UOM")
        var uom: String? = null,

        @SerializedName("Fill")
        var fill: String = "",
    )


}
