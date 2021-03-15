package com.fourofourfound.aims_delivery.network.loadList

import androidx.room.PrimaryKey
import com.fourofourfound.aims_delivery.database.entities.DatabaseLoad
import com.fourofourfound.aims_delivery.domain.Load
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
    val productDestination: String,
    val productQuantity: Double,
    val completed: Boolean = false,
    val tripId: String
)

fun List<NetworkLoadList>.asDomainModel(): List<Load> {
    return map {
        Load(
            _id = it._id,
            _v = it._v,
            productType = it.productType,
            productDestination = it.productDestination,
            productQuantity = it.productQuantity,
            completed = it.completed
        )
    }
}


/**
 * Convert Network results to database objects
 */
fun List<NetworkLoadList>.asDatabaseModel(): Array<DatabaseLoad> {
    return map {
        DatabaseLoad(
            _id = it._id,
            _v = it._v,
            productType = it.productType,
            productDestination = it.productDestination,
            productQuantity = it.productQuantity,
            completed = it.completed,
            tripId = it.tripId
        )
    }.toTypedArray()
}
