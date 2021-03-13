package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.fuel.Fuel
import com.fourofourfound.aims_delivery.database.entities.source.Source

data class SourceWithFuel(
    @Embedded val source: Source,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val fuel: Fuel
)