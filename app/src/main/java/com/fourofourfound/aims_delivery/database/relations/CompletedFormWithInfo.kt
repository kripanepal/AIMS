package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.*

data class CompletedFormWithInfo(
    @Embedded val form: DatabaseCompletionForm,

    @Relation(
        parentColumn = "identifier",
        entityColumn = "identifier",
    )
    val destination: DatabaseSourceOrSite,

    @Relation(
        parentColumn = "identifier",
        entityColumn = "identifier",
    )
    val location: DatabaseLocation,

    )