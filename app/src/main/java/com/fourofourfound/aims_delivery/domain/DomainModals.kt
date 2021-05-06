package com.fourofourfound.aims_delivery.domain

import android.os.Parcelable
import com.fourofourfound.aims_delivery.utils.DeliveryStatusEnum
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

/**
 * Trip
 * This class represents trip information used within the application.
 * @property tripId the id of the trip
 * @property tripName the name of the trip
 * @property tripDate the date the trip started or ended
 * @property sourceOrSite the current destination
 * @property deliveryStatus the status of the delivery
 * @constructor Create empty Trip
 */
@JsonClass(generateAdapter = true)
@Parcelize
data class Trip(
    var tripId: Int,
    var tripName: String,
    var tripDate: String,
    var sourceOrSite: List<SourceOrSite>,
    var deliveryStatus: DeliveryStatusEnum? = DeliveryStatusEnum.NOT_STARTED,
) : Parcelable

/**
 * Source or site
 * This class represents the destination information used within the application.
 * @property truckInfo the information of the truck
 * @property trailerInfo the information of the trailer
 * @property seqNum the sequence number of the destinations
 * @property wayPointTypeDescription the destination type(source or site)
 * @property location the location of the destination
 * @property productInfo the information of the product
 * @property siteContainerCode the code of the site container
 * @property siteContainerDescription the description of the site container
 * @property delReqNum delivery requested number
 * @property delReqLineNum delivery requested line number
 * @property sourceId the id of the source
 * @property siteId the id of the site
 * @property deliveryStatus the status of the delivery
 * @constructor Create empty Source or site
 */
@Parcelize
data class SourceOrSite(
    var truckInfo: TruckInfo,
    var trailerInfo: TrailerInfo,
    var seqNum: Int,
    var wayPointTypeDescription: String,
    var location: DestinationLocation,
    var productInfo: ProductInfo,
    var siteContainerCode: String? = null,
    var siteContainerDescription: String? = null,
    var delReqNum: Int? = null,
    var delReqLineNum: Int? = null,
    var sourceId:Int?,
    var siteId:Int?,
    var deliveryStatus: DeliveryStatusEnum,
) : Parcelable

/**
 * Truck info
 * This class represents the truck information
 * @property truckId the id of the truck
 * @property truckCode the code of the truck
 * @property truckDesc the description of the truck
 * @constructor Create empty Truck info
 */
@Parcelize
data class TruckInfo(
    var truckId: Int,
    var truckCode: String,
    var truckDesc: String
) : Parcelable

/**
 * Trailer info
 * This class represents the trailer information.
 * @property trailerId the id of the trailer
 * @property trailerCode the code of the trailer
 * @property trailerDesc the description of the trailer
 * @property fuelQuantity the quantity of the fuel
 * @constructor Create empty Trailer info
 */
@Parcelize
data class TrailerInfo(
    var trailerId: Int,
    var trailerCode: String,
    var trailerDesc: String,
    var fuelQuantity: Double = 0.0
) : Parcelable

/**
 * Destination location
 * This class represents the destination information
 * @property latitude the latitude of the destination
 * @property longitude the longitude of the location
 * @property destinationCode the code of the destination
 * @property destinationName the name of the destination
 * @property address1 the street address of the destination
 * @property city the destination city
 * @property stateAbbrev the destination state
 * @property postalCode the postal code of the destination
 * @constructor Create empty Destination location
 */
@Parcelize
data class DestinationLocation(
    var latitude: Double,
    var longitude: Double,
    var destinationCode: String,
    var destinationName: String,
    var address1: String,
    var city: String,
    var stateAbbrev: String,
    var postalCode: Int,
) : Parcelable

/**
 * Product info
 * This class represents the product information
 * @property productId the id of the product
 * @property productCode the code of the product
 * @property productDesc the description of the product
 * @property requestedQty the requested quantity of the fuel
 * @property uom the unit of measurement of the fuel
 * @property fill the note for the trip
 * @constructor Create empty Product info
 */
@Parcelize
data class ProductInfo(
    var productId: Int,
    var productCode: String? = null,
    var productDesc: String? = null,
    var requestedQty: Int? = null,
    var uom: String? = null,
    var fill: String = "",
) : Parcelable

/**
 * Geo coordinates
 * This class represents the geo coordinate
 * @property latitude latitude
 * @property longitude longitude
 * @constructor Create empty Geo coordinates
 */
data class GeoCoordinates(var latitude: Double,var longitude: Double)
