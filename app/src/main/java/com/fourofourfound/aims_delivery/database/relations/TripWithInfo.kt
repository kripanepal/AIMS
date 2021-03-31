package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.DatabaseSourceOrSite
import com.fourofourfound.aims_delivery.database.entities.DatabaseTrip

data class TripWithInfo(
    @Embedded val trip: DatabaseTrip,
    @Relation(
        parentColumn = "tripId",
        entityColumn = "tripId",
        entity = DatabaseSourceOrSite::class
    )
    val destinationInfo: List<DestinationWithInfo>
)