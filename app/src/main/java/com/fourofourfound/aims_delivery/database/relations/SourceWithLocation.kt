package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.location.LocationWithAddress
import com.fourofourfound.aims_delivery.database.entities.source.Source

data class SourceWithLocation(
    @Embedded val source: Source,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val location: LocationWithAddress
)