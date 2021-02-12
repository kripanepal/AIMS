package com.fourofourfound.aims_delivery.network.tripList

import com.fourofourfound.aims_delivery.database.entities.DatabaseTrip
import com.fourofourfound.aims_delivery.domain.Trip
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkTripListContainer(val trips: List<NetworkTripList>)


fun NetworkTripListContainer.asDomainModel(): List<Trip> {
    return trips.map {
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
fun NetworkTripListContainer.asDatabaseModel(): Array<DatabaseTrip> {
    return trips.map {
        DatabaseTrip(
            _id = it._id,
            name = it.name, _v = it._v
        )
    }.toTypedArray()
}




