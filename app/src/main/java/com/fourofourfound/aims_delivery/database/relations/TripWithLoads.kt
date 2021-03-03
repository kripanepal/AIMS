package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.load.DatabaseLoad
import com.fourofourfound.aims_delivery.database.entities.trip.DatabaseTrip


data class TripWithLoads(
    @Embedded val trip:DatabaseTrip,
    @Relation(parentColumn = "_id", entityColumn = "tripId")
    val loads:List<DatabaseLoad>
)