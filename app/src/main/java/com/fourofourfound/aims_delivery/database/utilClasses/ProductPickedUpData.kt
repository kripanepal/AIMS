package com.fourofourfound.aims_delivery.database.utilClasses

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName
import java.util.*

data class ProductPickedUpData
    (
    val driverCode: String,

    val tripId: Int,

    val sourceId: Int,

    val productId: Int,

    val billOfLadingNumber: String,

    val startTime: Calendar,

    val endTime: Calendar,
    val grossQty: Int,
    val netQty: Int,
)