package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseFuel(
    @PrimaryKey
    var productId: Int?,
    var productCode: String?,
    var productDesc: String?
) {
    init {
        if (productId == null) productId = 759
        if (productCode == null) productCode = "NOT PROVIDED"
        if (productDesc == null) productDesc = "NOT PROVIDED"

        //TODO TEMP FIX NEED TO CHANGE
        if (productId == 0) productId = 759
    }
}