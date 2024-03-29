package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.DatabaseSourceOrSite
import com.fourofourfound.aims_delivery.database.entities.DatabaseTrip
import com.fourofourfound.aims_delivery.domain.*
import com.fourofourfound.aims_delivery.network.NetworkTrip

/**
 * Trip with info
 * This class represents the relation between trip and destinations.
 * @property trip the trip information
 * @property destinationInfo the destination information
 * @constructor Create empty Trip with info
 */
data class TripWithInfo(
    @Embedded val trip: DatabaseTrip,
    @Relation(
        parentColumn = "tripId",
        entityColumn = "tripId",
        entity = DatabaseSourceOrSite::class
    )
    val destinationInfo: List<DestinationWithInfo>
)

/**
 * As network model
 * This class converts the trip information from database to network model.
 * @return the converted trip information
 */
fun List<TripWithInfo>.asNetworkModel(): List<NetworkTrip> {
    var finalList = mutableListOf<NetworkTrip>()
    map {
        for (each in it.destinationInfo)
            each.sourceOrSite.apply {
                finalList.add(
                    NetworkTrip(
                        truckId,
                        each.truck.truckCode,
                        each.truck.truckDesc,
                        each.trailer.trailerId,
                        each.trailer.trailerCode,
                        each.trailer.trailerDesc,
                        each.trip.tripId,
                        each.trip.tripName,
                        each.trip.tripDate,
                        seqNum,
                        wayPointTypeDescription,
                        each.location.latitude,
                        each.location.longitude,
                        each.location.destinationCode,
                        each.location.destinationName,
                        each.location.address1,
                        each.location.address2,
                        each.location.city,
                        each.location.stateAbbrev,
                        each.location.postalCode,
                        siteContainerCode,
                        siteContainerDescription,
                        delReqNum,
                        delReqLineNum,
                        productId,
                        each.fuel.productCode,
                        each.fuel.productDesc,
                        requestedQty,
                        uom,
                        fill,
                        sourceId = sourceId,
                        siteId = siteId
                    )
                )
            }
    }
    return finalList
}

/**
 * As domain model
 * This class converts the trip information from database to domain model.
 * @return the converted trip information
 */
fun List<TripWithInfo>.asDomainModel(): List<Trip> {
    var listOfSourceAndSite: MutableList<SourceOrSite>
    var finalList = mutableListOf<Trip>()
    var productInfo: ProductInfo
    var destinationLocation: DestinationLocation
    var trailerInfo: TrailerInfo
    var truckInfo: TruckInfo
    var trip: Trip
    map {
        listOfSourceAndSite = mutableListOf()
        for (destination in it.destinationInfo) {
            productInfo = ProductInfo(
                destination.fuel.productId,
                destination.fuel.productCode,
                destination.fuel.productDesc,
                destination.sourceOrSite.requestedQty,
                destination.sourceOrSite.uom,
                destination.sourceOrSite.fill
            )

            destination.location.apply {
                destinationLocation = DestinationLocation(
                    latitude,
                    longitude,
                    destinationCode,
                    destinationName,
                    address1,
                    city,
                    stateAbbrev,
                    postalCode
                )
            }
            destination.trailer.apply {
                trailerInfo =
                    TrailerInfo(trailerId, trailerCode, trailerDesc, fuelQuantity.toDouble())
            }

            destination.truck.apply {
                truckInfo = TruckInfo(truckId, truckCode, truckDesc)
            }
            destination.sourceOrSite.apply {
                listOfSourceAndSite.add(
                    SourceOrSite(
                        truckInfo,
                        trailerInfo,
                        seqNum,
                        wayPointTypeDescription,
                        destinationLocation,
                        productInfo,
                        siteContainerCode,
                        siteContainerDescription,
                        delReqNum,
                        delReqLineNum,
                        sourceId, siteId, deliveryStatus
                    )
                )
            }
        }
        it.trip.apply {
            trip = Trip(tripId, tripName, tripDate, listOfSourceAndSite, deliveryStatus)
        }
        finalList.add(trip)
    }
    return finalList
}
