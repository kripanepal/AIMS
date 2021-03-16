package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.DatabaseSite
import com.fourofourfound.aims_delivery.database.entities.DatabaseSource
import com.fourofourfound.aims_delivery.database.entities.DatabaseTrip
import com.fourofourfound.aims_delivery.domain.Trip

class DatabaseTripsWithInfo(
    @Embedded val databaseTrips: DatabaseTrip,
    @Relation(
        parentColumn = "tripId",
        entityColumn = "tripId",
        entity = DatabaseSource::class,
    )
    val source: List<SourceWithLocationAndFuel>,
    @Relation(
        parentColumn = "tripId",
        entityColumn = "tripId",
        entity = DatabaseSite::class
    )
    val site: List<SiteWithLocationAndFuel>
)

fun List<DatabaseTripsWithInfo>.asDomainModel(): List<Trip> {
    return map {
        Trip(
            it.databaseTrips.tripId,
            it.databaseTrips.tripLog,
            it.databaseTrips.truckID,
            it.databaseTrips.truckName,
            it.databaseTrips.travelID,
            it.databaseTrips.travelType,
            it.source.asDomainModel(),
            it.site.asDomainModel()
        )
    }
}