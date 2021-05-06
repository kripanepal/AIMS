package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database trailer
 * This class represents the trailer information that is saved in database
 * @property trailerId the id of the trailer
 * @property trailerCode the code of the trailer
 * @property trailerDesc the description of the trailer
 * @property fuelQuantity the fuel quantity
 * @constructor Create empty Database trailer
 */
@Entity
data class DatabaseTrailer(
    @PrimaryKey
    var trailerId: Int,
    var trailerCode: String,
    var trailerDesc: String,
    var fuelQuantity: Int = 0
)

