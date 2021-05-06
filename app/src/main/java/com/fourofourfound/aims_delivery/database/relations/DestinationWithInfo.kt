package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.*

/**
 * Destination with info
 * This class represents the relation between different entities.
 * @property sourceOrSite the destination information
 * @property location the location information
 * @property trailer the trailer information
 * @property fuel the fuel information
 * @property trip the trip information
 * @property truck the truck information
 * @constructor Create empty Destination with info
 */
data class DestinationWithInfo(
    @Embedded val sourceOrSite: DatabaseSourceOrSite,

    @Relation(
        parentColumn = "identifier",
        entityColumn = "identifier",
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