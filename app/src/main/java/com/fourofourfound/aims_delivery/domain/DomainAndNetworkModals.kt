package com.fourofourfound.aims_delivery.domain

import android.os.Parcelable
import com.fourofourfound.aims_delivery.database.entities.*
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Quantity(
    val volume: Int,
    val measure: String
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class Fuel(
    val type: String,
    val quantity: Quantity
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class Coordinates(
    val lat: Double,
    val long: Double
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class Location(
    val street: String,
    val city: String,
    val state: String,
    val zip: Int,
    val gps: Coordinates
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class NetworkSite(
    val siteID: String,
    val name: String,
    val location: Location,
    val fuel: Fuel
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class Source(
    val sourceID: String,
    val name: String,
    val location: Location,
    val fuel: Fuel,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class Site(
    val siteID: String,
    val name: String,
    val location: Location,
    val fuel: Fuel,
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class Trip(
    val tripID: String,
    val tripLog: String,
    val truckID: String,
    val truckName: String,
    val travelID: String,
    val travelType: String,
    val source: List<Source>,
    val site: List<Site>,
) : Parcelable {

    val status: String = "Not Started"
}

fun Quantity.asDatabaseModel(): DatabaseFuelQuantity = DatabaseFuelQuantity(volume, measure)

fun Fuel.asDatabaseModel(sourceOrSiteId: String): DatabaseFuel {
    return DatabaseFuel(type, quantity.asDatabaseModel(), sourceOrSiteId)
}

fun Coordinates.asDatabaseModel(): DatabaseCoordinates =
    DatabaseCoordinates(latitude = lat, longitude = long)

fun Location.asDatabaseModel(sourceOrSiteId: String): DatabaseLocationWithAddress {
    return DatabaseLocationWithAddress(
        street,
        city,
        state,
        zip,
        gps.asDatabaseModel(),
        sourceOrSiteId
    )
}


fun Trip.asDatabaseModel(): DatabaseTrip {
    return DatabaseTrip(
        tripID,
        tripLog,
        truckID, truckName, travelID, travelType
    )
}


fun List<Trip>.asDatabaseModel(): Array<DatabaseTrip> {
    return map {
        DatabaseTrip(
            tripId = it.tripID,
            it.tripLog,
            it.truckID,
            it.truckName,
            it.travelID,
            it.travelType
        )

    }.toTypedArray()
}

fun List<Source>.asDatabaseModel(tripId: String): List<DatabaseSource> {
    return map {
        DatabaseSource(
            it.sourceID, it.name, tripId
        )

    }
}

fun List<Site>.asDomainModel(tripId: String): List<DatabaseSite> {
    return map {
        DatabaseSite(
            it.siteID, it.name, tripId
        )

    }
}