package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.*

/**
 * Completed form with info
 * This class represents the relation between the form, destination, location, images and product
 * @property form the from information
 * @property destination the destination information
 * @property location the location information
 * @property images the bill of the lading images
 * @property product the product information
 * @constructor Create empty Completed form with info
 */
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

    @Relation(
        parentColumn = "identifier",
        entityColumn = "identifier",
    )
    val images: List<BillOfLadingImages>,

    @Relation(
        parentColumn = "productDelivered",
        entityColumn = "productId",
    )
    val product: DatabaseFuel

)