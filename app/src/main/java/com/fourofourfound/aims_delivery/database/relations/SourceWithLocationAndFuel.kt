package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.Fuel
import com.fourofourfound.aims_delivery.database.entities.Source
import com.fourofourfound.aims_delivery.database.entities.location.LocationWithAddress

data class SourceWithLocationAndFuel(
    @Embedded val source: Source,
    @Relation(
        parentColumn = "sourceId",
        entityColumn = "sourceOrSiteId",
        entity = LocationWithAddress::class
    )
    val location: LocationWithAddress?,

    @Relation(
        parentColumn = "sourceId",
        entityColumn = "sourceOrSiteId",
        entity = Fuel::class
    )
    val fuel: Fuel?
)