package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import retrofit2.http.Path

@Entity(primaryKeys = ["tripId","statusCode"])
data class DatabaseStatusPut(
    val driverCode: String,
    val tripId: Int,
    val statusCode: String,
    val statusMessage: String,
    val statusDate: String,)