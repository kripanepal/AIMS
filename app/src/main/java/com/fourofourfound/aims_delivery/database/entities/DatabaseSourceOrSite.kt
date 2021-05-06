package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import com.fourofourfound.aims_delivery.utils.DeliveryStatusEnum

/**
 * Database source or site
 * This class represents the destination information that is saved in database
 * @property tripId the id of the trip
 * @property truckId the id of the truck
 * @property trailerId the id of the trailer
 * @property productId the id of the product
 * @property destinationCode the code of the destination
 * @property seqNum the sequence number of the delivery
 * @property wayPointTypeDescription the destination type
 * @property siteContainerCode the code of the site container
 * @property siteContainerDescription the description of the site
 * @property delReqNum delivery requested number
 * @property delReqLineNum delivery requested line number
 * @property requestedQty the requested quantity of the fuel
 * @property uom the unit of measurement
 * @property fill the desired fill label
 * @property sourceId the id of the source
 * @property siteId the id of the site
 * @property identifier the key the identifies the destination
 * @property deliveryStatus the status of the delivery
 * @constructor Create empty Database source or site
 */
@Entity(
    primaryKeys = ["tripId", "seqNum"]
)
data class DatabaseSourceOrSite(
    var tripId: Int,
    var truckId: Int,
    var trailerId: Int,
    var productId: Int,
    var destinationCode: String,
    var seqNum: Int,
    var wayPointTypeDescription: String,
    var siteContainerCode: String? = null,
    var siteContainerDescription: String? = null,
    var delReqNum: Int? = null,
    var delReqLineNum: Int? = null,
    var requestedQty: Int = 0,
    var uom: String = "NOT PROVIDED",
    var fill: String = "",
    var sourceId: Int?,
    var siteId: Int?,
    var identifier: String = "$tripId $seqNum",
    var deliveryStatus: DeliveryStatusEnum = DeliveryStatusEnum.NOT_STARTED,
)

