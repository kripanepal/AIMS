package com.fourofourfound.aims_delivery.domain

import android.os.Parcelable
import com.fourofourfound.aims_delivery.utils.StatusEnum
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Trip(
    var tripId: Int,
    var tripName: String,
    var tripDate: String,
    var sourceOrSite: List<SourceOrSite>,
    val status: StatusEnum? = StatusEnum.NOT_STARTED,
) : Parcelable

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

    var status: StatusEnum,
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

@Parcelize
data class ProductInfo(
    var productId: Int,
    var productCode: String? = null,
    var productDesc: String? = null,
    var requestedQty: Int? = null,
    var uom: String? = null,
    var fill: String = "",
) : Parcelable

