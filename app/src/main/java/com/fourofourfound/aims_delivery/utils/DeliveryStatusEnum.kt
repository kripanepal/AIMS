package com.fourofourfound.aims_delivery.utils


/**
 * Delivery status enum
 * This class represents status of a trip or a destination
 * @property status status of a trip
 * @constructor
 */
enum class DeliveryStatusEnum(val status: Int) {
    ONGOING(0),
    NOT_STARTED(1),
    COMPLETED(2)
}