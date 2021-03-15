package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.Fuel
import com.fourofourfound.aims_delivery.database.entities.Site
import com.fourofourfound.aims_delivery.database.entities.location.LocationWithAddress

data class SiteWithLocationAndFuel(
    @Embedded val site: Site,
    @Relation(
        parentColumn = "siteId",
        entityColumn = "sourceOrSiteId"
    )
    val location: LocationWithAddress,
    @Relation(
        parentColumn = "siteId",
        entityColumn = "sourceOrSiteId"
    )
    val fuel: Fuel
)