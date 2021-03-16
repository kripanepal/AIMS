package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.DatabaseFuel
import com.fourofourfound.aims_delivery.database.entities.DatabaseLocationWithAddress
import com.fourofourfound.aims_delivery.database.entities.DatabaseSource
import com.fourofourfound.aims_delivery.database.entities.asDomainModel
import com.fourofourfound.aims_delivery.domain.Source

data class SourceWithLocationAndFuel(
    @Embedded val source: DatabaseSource,
    @Relation(
        parentColumn = "sourceId",
        entityColumn = "sourceOrSiteId",
        entity = DatabaseLocationWithAddress::class
    ) val location: DatabaseLocationWithAddress,

    @Relation(
        parentColumn = "sourceId",
        entityColumn = "sourceOrSiteId",
        entity = DatabaseFuel::class
    )
    val fuel: DatabaseFuel
)

fun List<SourceWithLocationAndFuel>.asDomainModel(): List<Source> {
    return map {

        Source(
            it.source.sourceId,
            it.source.name,
            it.location.asDomainModel(),
            it.fuel.asDomainModel()
        )

    }
}
