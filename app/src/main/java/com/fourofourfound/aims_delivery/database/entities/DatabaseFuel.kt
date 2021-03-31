package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseFuel(
    @PrimaryKey
    val productId: Int = 759,
    val productCode: String? = "NOT PROVIDED",
    val productDesc: String
)