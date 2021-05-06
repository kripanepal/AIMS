package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database fuel
 * This class represents the fuel information that is saved in database
 * @property productId the id of the product
 * @property productCode the code of the product
 * @property productDesc the description of the product
 * @constructor Create empty Database fuel
 */
@Entity
data class DatabaseFuel(
    @PrimaryKey
    val productId: Int = 759,
    val productCode: String? = "NOT PROVIDED",
    val productDesc: String? = "NOT PROVIDED"
)