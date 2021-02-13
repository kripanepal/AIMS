package com.fourofourfound.aims_delivery.network.tripList

import com.fourofourfound.aims_delivery.database.entities.DatabaseTrip
import com.fourofourfound.aims_delivery.domain.Trip
import com.squareup.moshi.JsonClass

/**
 * trips represent a trip info that can be displayed.
 */
@JsonClass(generateAdapter = true)
data class NetworkTripList(
    val _id: String,
    val name: String, val _v: Int = 0
)


fun List<NetworkTripList>.asDomainModel(): List<Trip> {
    return map {
        Trip(
            _id = it._id,
            name = it.name,
            _v = it._v,
            completed = false

        )
    }
}


/**
 * Convert Network results to database objects
 */
fun List<NetworkTripList>.asDatabaseModel(): Array<DatabaseTrip> {
    return map {
        DatabaseTrip(
            _id = it._id,
            name = it.name, _v = it._v
        )
    }.toTypedArray()
}