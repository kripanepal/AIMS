package com.fourofourfound.aims_delivery.network

import com.google.gson.annotations.SerializedName

/**
 * Network trip
 * This class represents the response from restful api containing the trip info.
 * @property truckId the id of the truck
 * @property truckCode the code of the truck
 * @property truckDesc the description of the truck
 * @property trailerId the id of the trailer
 * @property trailerCode the code of the trailer
 * @property trailerDesc the description of the trailer
 * @property tripId the id of the trip
 * @property tripName the name of the trip
 * @property tripDate the date of the trip
 * @property seqNum the seq number of the destination
 * @property waypointTypeDescription destination type
 * @property latitude the latitude of the destination
 * @property longitude the longitude of the destination
 * @property destinationCode the code of the destination
 * @property destinationName the name of the destination
 * @property address1 the street address of the destination
 * @property address2 the address of the destination
 * @property city the destination city
 * @property stateAbbrev the destination state
 * @property postalCode the postal code of the destination
 * @property siteContainerCode the code of site container
 * @property siteContainerDescription the description of the site container
 * @property delReqNum delivery requested number
 * @property delReqLineNum delivery requested line number
 * @property productId the id of the product
 * @property productCode the code of the product
 * @property productDesc the description of the product
 * @property requestedQty the requested quantity of the fuel
 * @property uom the unit of measurement
 * @property fill the desired fill label
 * @property driverCode the code of the driver
 * @property driverName the name of the driver
 * @property sourceId the id of the source
 * @property siteId the id of the site
 * @constructor Create empty Network trip
 */
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
    val sourceId: Int?,

    @SerializedName("SiteID")
    val siteId: Int?,
)

/**
 * As filtered
 * This method takes a network trip and substitutes null value with given value.
 * @return the modified network trip
 */
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
        siteContainerCode,
        siteContainerDescription,
        delReqNum,
        delReqLineNum,
        productId ?: 1,
        productCode ?: "Not provided",
        productDesc ?: "Not provided",
        requestedQty ?: 0,
        uom ?: "",
        fill ?: "Not provided",
        sourceId = sourceId,
        siteId = siteId
    )

/**
 * Driver
 * This class represents the driver.
 * @property id the id of the driver
 * @property companyID the company id
 * @property code the code of the driver
 * @property driverName the name of the driver
 * @property driverDescription the description of the driver
 * @property compasDriverID the compas driver id
 * @property truckId the id of the truck
 * @property truckDescription the description of the truck
 * @property active the status of the driver
 * @constructor
 */
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



