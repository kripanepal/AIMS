package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.DatabaseFuel
import com.fourofourfound.aims_delivery.database.entities.DatabaseSite
import com.fourofourfound.aims_delivery.database.entities.location.DatabaseLocationWithAddress

data class SiteWithLocationAndFuel(
    @Embedded val site: DatabaseSite,
    @Relation(
        parentColumn = "siteId",
        entityColumn = "sourceOrSiteId",
        entity = DatabaseLocationWithAddress::class
    )
    val location: DatabaseLocationWithAddress?,
    @Relation(
        parentColumn = "siteId",
        entityColumn = "sourceOrSiteId",
        entity = DatabaseFuel::class
    )
    val fuel: DatabaseFuel?
)