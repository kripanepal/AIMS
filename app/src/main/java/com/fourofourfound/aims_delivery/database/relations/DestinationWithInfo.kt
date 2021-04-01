package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.*

data class DestinationWithInfo(
    @Embedded val sourceOrSite: DatabaseSourceOrSite,
    @Relation(
        parentColumn = "productId",
        entityColumn = "productId",
    )
    val product: DatabaseFuel,
    @Relation(
        parentColumn = "destinationCode",
        entityColumn = "destinationCode",
    )
    val location: DatabaseLocation,
    @Relation(
        parentColumn = "trailerId",
        entityColumn = "trailerId",
    )
    val trailer: DatabaseTrailer,
    @Relation(
        parentColumn = "productId",
        entityColumn = "productId",
    )
    val fuel: DatabaseFuel,

    @Relation(
        parentColumn = "tripId",
        entityColumn = "tripId",
    )
    val trip: DatabaseTrip,

    @Relation(
        parentColumn = "truckId",
        entityColumn = "truckId",
    )
    val truck: DatabaseTruck,


    )