package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.DatabaseFuel
import com.fourofourfound.aims_delivery.database.entities.DatabaseSource
import com.fourofourfound.aims_delivery.database.entities.location.DatabaseLocationWithAddress

data class SourceWithLocationAndFuel(
    @Embedded val source: DatabaseSource,
    @Relation(
        parentColumn = "sourceId",
        entityColumn = "sourceOrSiteId",
        entity = DatabaseLocationWithAddress::class
    )
    val location: DatabaseLocationWithAddress?,

    @Relation(
        parentColumn = "sourceId",
        entityColumn = "sourceOrSiteId",
        entity = DatabaseFuel::class
    )
    val fuel: DatabaseFuel?
)