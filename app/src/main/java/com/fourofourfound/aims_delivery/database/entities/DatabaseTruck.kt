package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database truck
 * This class represents the truck information that is saved in database.
 * @property truckId the id of the truck
 * @property truckCode the code of the truck
 * @property truckDesc the description of the truck
 * @constructor Create empty Database truck
 */
@Entity
data class DatabaseTruck(
    @PrimaryKey
    var truckId: Int,
    var truckCode: String,
    var truckDesc: String,
)