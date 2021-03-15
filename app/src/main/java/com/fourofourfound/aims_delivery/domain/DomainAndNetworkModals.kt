package com.fourofourfound.aims_delivery.domain

import com.fourofourfound.aims_delivery.database.entities.*
import com.fourofourfound.aims_delivery.database.entities.location.DatabaseCoordinates
import com.fourofourfound.aims_delivery.database.entities.location.DatabaseLocationWithAddress


data class Quantity(
    val volume: Int,
    val measure: String
)

data class Fuel(
    val type: String,
    val quantity: Quantity
)

data class Coordinates(
    val lat: Double,
    val long: Double
)

data class Location(
    val street: String,
    val city: String,
    val state: String,
    val zip: Int,
    val gps: Coordinates
)

data class NetworkSite(
    val siteID: String,
    val name: String,
    val location: Location,
    val fuel: Fuel
)

data class Source(
    val sourceID: String,
    val name: String,
    val location: Location,
    val fuel: Fuel,
)

data class Site(
    val siteID: String,
    val name: String,
    val location: Location,
    val fuel: Fuel,
)

data class Tripf(
    val tripID: String,
    val tripLog: String,
    val truckID: String,
    val truckName: String,
    val travelID: String,
    val travelType: String,
    val source: List<Source>,
    val site: List<Site>
)

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


fun Tripf.asDatabaseModel(): DatabaseTripf {
    return DatabaseTripf(
        tripID,
    )
}


fun List<Tripf>.asDatabaseModel(): Array<DatabaseTripf> {
    return map {
        DatabaseTripf(
            tripId = it.tripID,
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