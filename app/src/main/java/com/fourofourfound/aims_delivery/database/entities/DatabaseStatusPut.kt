package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity

/**
 * Database status put
 * This class represents the status information about a delivery that is saved in database.
 * @property driverCode the code of the driver
 * @property tripId the id of the trip
 * @property statusCode the code of the status
 * @property statusMessage the status message
 * @property statusDate the date the status was generated
 * @constructor Create empty Database status put
 */
@Entity(primaryKeys = ["tripId","statusCode"])
data class DatabaseStatusPut(
    val driverCode: String,
    val tripId: Int,
    val statusCode: String,
    val statusMessage: String,
    val statusDate: String,)