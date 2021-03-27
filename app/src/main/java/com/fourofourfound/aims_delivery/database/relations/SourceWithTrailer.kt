package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.DatabaseSourceOrSite
import com.fourofourfound.aims_delivery.database.entities.DatabaseTrailer

class SourceWithTrailer(
    @Embedded val source: DatabaseSourceOrSite,
    @Relation(
        parentColumn = "trailerId",
        entityColumn = "trailerId"
    )
    val trailer: DatabaseTrailer,
)