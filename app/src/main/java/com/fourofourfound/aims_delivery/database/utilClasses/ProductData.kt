package com.fourofourfound.aims_delivery.database.utilClasses

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName
import java.util.*

data class ProductData
    (
    val driverCode: String,

    val tripId: Int,

    val sourceOrSiteId: Int,

    val productId: Int,

    val billOfLadingNumber: String,

    val startTime: Calendar,

    val endTime: Calendar,
    val grossQty: Int,
    val netQty: Int,
    val trailerRemainingQuantity:Double,
    val type:String
)