package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.DatabaseTrips
import com.fourofourfound.aims_delivery.database.entities.Source

class DatabaseTripsWithInfo(
    @Embedded val databaseTrips: DatabaseTrips,
    @Relation(
        parentColumn = "tripId",
        entityColumn = "tripId",
        entity = Source::class,
    )
    val source: List<SourceWithLocationAndFuel>,
//    @Relation(
//        parentColumn = "tripId",
//        entityColumn = "tripId",
//        entity = Site::class
//    )
//    val site: List<SiteWithLocationAndFuel>
)