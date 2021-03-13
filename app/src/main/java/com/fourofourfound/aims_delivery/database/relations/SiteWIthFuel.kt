package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.fuel.Fuel
import com.fourofourfound.aims_delivery.database.entities.site.Site

data class SiteWithFuel(
    @Embedded val site: Site,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val fuel: Fuel
)