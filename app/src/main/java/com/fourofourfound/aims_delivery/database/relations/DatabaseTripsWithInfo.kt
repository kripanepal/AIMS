package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.DatabaseSourceOrSite
import com.fourofourfound.aims_delivery.database.entities.DatabaseTrip
import com.fourofourfound.aims_delivery.database.entities.DatabaseTruck

class DatabaseTripsWithInfo(
    @Embedded val tripInfo: DatabaseTrip,
    @Relation(
        parentColumn = "tripId",
        entityColumn = "tripId",

        ) val truck: DatabaseTruck,
    @Relation(
        parentColumn = "tripId",
        entityColumn = "tripId",
        entity = DatabaseSourceOrSite::class
    )
    val sourceOrSite: List<SourceWithTrailer>,
)
