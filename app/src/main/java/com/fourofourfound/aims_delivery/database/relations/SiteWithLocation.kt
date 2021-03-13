package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.location.LocationWithAddress
import com.fourofourfound.aims_delivery.database.entities.site.Site

data class SiteWithLocation(
    @Embedded val site: Site,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val location: LocationWithAddress
)