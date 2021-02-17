package com.fourofourfound.aims_delivery.network.loadList

import androidx.room.PrimaryKey
import com.fourofourfound.aims_delivery.database.entities.trip.DatabaseTrip
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.network.tripList.NetworkTripList
import com.squareup.moshi.JsonClass

/**
 * trips represent a trip info that can be displayed.
 */
@JsonClass(generateAdapter = true)
data class NetworkLoadList(
    @PrimaryKey
    val _id: String,
    val _v: Int = 0,

    val productType: String,
    val productDestination:String,
    val productQuantity:Double,
    val completed: Boolean =false
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
