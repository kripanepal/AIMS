package com.fourofourfound.aims_delivery.network

import com.google.gson.annotations.SerializedName

data class NetworkTrip(
    @SerializedName("TruckId")
    val truckId: Int?,

    @SerializedName("TruckCode")
    val truckCode: String?,

    @SerializedName("TruckDesc")
    val truckDesc: String?,

    @SerializedName("TrailerId")
    val trailerId: Int?,

    @SerializedName("TrailerCode")
    val trailerCode: String?,

    @SerializedName("TrailerDesc")
    val trailerDesc: String?,

    @SerializedName("TripId")
    val tripId: Int?,

    @SerializedName("TripName")
    val tripName: String?,

    @SerializedName("TripDate")
    val tripDate: String?,

    @SerializedName("SeqNum")
    val seqNum: Int?,

    @SerializedName("WaypointTypeDescription")
    val waypointTypeDescription: String?,

    @SerializedName("Latitude")
    val latitude: Double?,

    @SerializedName("Longitude")
    val longitude: Double?,

    @SerializedName("DestinationCode")
    val destinationCode: String?,

    @SerializedName("DestinationName")
    val destinationName: String?,

    @SerializedName("Address1")
    val address1: String?,

    @SerializedName("Address2")
    val address2: String?,

    @SerializedName("City")
    val city: String?,

    @SerializedName("StateAbbrev")
    val stateAbbrev: String?,

    @SerializedName("PostalCode")
    val postalCode: Int?,

    @SerializedName("SiteContainerCode")
    val siteContainerCode: String?,

    @SerializedName("SiteContainerDescription")
    val siteContainerDescription: String?,

    @SerializedName("DelReqNum")
    val delReqNum: Int?,

    @SerializedName("DelReqLineNum")
    val delReqLineNum: Int?,

    @SerializedName("ProductId")
    val productId: Int?,

    @SerializedName("ProductCode")
    val productCode: String?,

    @SerializedName("ProductDesc")
    val productDesc: String?,

    @SerializedName("RequestedQty")
    val requestedQty: Int?,

    @SerializedName("UOM")
    val uom: String?,

    @SerializedName("Fill")
    val fill: String?,

    @SerializedName("DriverCode")
    val driverCode: String = "N/A",

    @SerializedName("DriverName")
    val driverName: String = "N/A",

    @SerializedName("SourceID")
    val sourceId: Int? ,

    @SerializedName("SiteID")
    val siteId: Int?,
)

fun NetworkTrip.asFiltered(): NetworkTrip =
            NetworkTrip(
                truckId ?: 0,
                truckCode ?: "N/A",
                truckDesc ?: "N/A",
                trailerId ?: 0,
                trailerCode ?: "0",
                trailerDesc ?: "N/A",
                tripId ?: 0,
                tripName ?: "N/A",
                tripDate ?: "N/A",
                seqNum ?: -1,
                waypointTypeDescription ?: "Site container",
                latitude ?: 0.0,
                longitude ?: 0.0,
                destinationCode ?: "N/A",
                destinationName ?: "N/A",
                address1 ?: "N/A",
                address2 ?: "N/A",
                city ?: "N/A",
                stateAbbrev ?: "N/A",
                postalCode ?: 0,
                siteContainerCode ,
                siteContainerDescription ,
                delReqNum,
                delReqLineNum ,
                productId ?: 1,
                productCode ?: "Not provided",
                productDesc ?: "Not provided",
                requestedQty ?: 0,
                uom ?: "",
                fill ?: "Not provided",
                sourceId= sourceId,
                siteId =  siteId
            )

data class Driver(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("CompanyID")
    val companyID: Int,
    @SerializedName("Code")
    val code: String,
    @SerializedName("DriverName")
    val driverName: String,
    @SerializedName("DriverDescription")
    val driverDescription: String,
    @SerializedName("CompasDriverID")
    val compasDriverID: String?,
    @SerializedName("TruckId")
    val truckId: Int?,
    @SerializedName("TuckDescription")
    val truckDescription: String?,
    @SerializedName("Active")
    val active: Boolean?
)



