package com.fourofourfound.aims_delivery.domain

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Trip(

    var truckInfo: TruckInfo,
    var tripId: String,
    var tripName: String,
    var tripDate: String,
    var sourceOrSite: List<SourceOrSite>,
    val status: String = "NOT_STARTED",
) : Parcelable

@Parcelize
data class SourceOrSite(
    var trailerInfo: TrailerInfo,
    var seqNum: Int,
    var waypointTypeDescription: String,
    var latitude: Double,
    var longitude: Double,
    var destinationCode: String,
    var destinationName: String,
    var address1: String,
    var city: String,
    var stateAbbrev: String,
    var postalCode: Int,


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


    val status: String = "NOT_STARTED",

    ) : Parcelable

@Parcelize
data class TruckInfo(
    var truckId: Int,
    var truckCode: String,
    var truckDesc: String
) : Parcelable

@Parcelize
data class TrailerInfo(
    var trailerId: Int,
    var trailerCode: String,
    var trailerDesc: String
) : Parcelable

