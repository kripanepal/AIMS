package com.fourofourfound.aims_delivery.database.utilClasses

import java.util.*

/**
 * Product data
 * This class represents the information about the product to be sent to the dispatcher.
 * @property driverCode the code of the driver
 * @property tripId the id of the trip
 * @property sourceOrSiteId the destination
 * @property productId the id of the product
 * @property billOfLadingNumber the bill of lading images
 * @property startTime the start time of the delivery
 * @property endTime the end time of the delivery
 * @property grossQty the gross quantity of the fuel
 * @property netQty the net quantity of the fuel
 * @property trailerRemainingQuantity the remaining fuel amount in the trailer
 * @property type the type of the fuel
 * @constructor Create empty Product data
 */
data class ProductData(
    val driverCode: String,
    val tripId: Int,
    val sourceOrSiteId: Int,
    val productId: Int,
    val billOfLadingNumber: String,
    val startTime: Calendar,
    val endTime: Calendar,
    val grossQty: Int,
    val netQty: Int,
    val trailerRemainingQuantity: Double,
    val type: String
)