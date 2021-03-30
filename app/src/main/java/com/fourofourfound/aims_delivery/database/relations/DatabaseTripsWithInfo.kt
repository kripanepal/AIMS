package com.fourofourfound.aims_delivery.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.fourofourfound.aims_delivery.database.entities.DatabaseSourceOrSite
import com.fourofourfound.aims_delivery.database.entities.DatabaseTrip
import com.fourofourfound.aims_delivery.database.entities.DatabaseTruck
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.domain.TrailerInfo
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.domain.TruckInfo
import com.fourofourfound.aims_delivery.network.NetworkTrip

data class DatabaseTripsWithInfo(
    @Embedded val tripInfo: DatabaseTrip,
    @Relation(
        parentColumn = "tripId",
        entityColumn = "tripId",

        ) val truck: DatabaseTruck,
    @Relation(
        parentColumn = "tripId",
        entityColumn = "tripId",
        entity = DatabaseSourceOrSite::class
    )
    val sourceOrSite: List<SourceWithTrailer>,
)


fun List<DatabaseTripsWithInfo>.asDomainModel(): List<Trip> {
    var listOfSourceAndSite = mutableListOf<SourceOrSite>()
    var finalList = mutableListOf<Trip>()
    map {
        for (each in it.sourceOrSite) {
            each.apply {
                var trailer =
                    TrailerInfo(trailer.trailerId, trailer.trailerCode, trailer.trailerDesc)
                source.apply {
                    var sourceOrSite = SourceOrSite(
                        trailer,
                        seqNum,
                        wayPointTypeDescription,
                        latitude,
                        longitude,
                        destinationCode,
                        destinationName,
                        address1,
                        city,
                        stateAbbrev,
                        postalCode,
                        siteContainerCode,
                        siteContainerDescription,
                        delReqNum,
                        delReqLineNum,
                        productId,
                        productCode,
                        productDesc,
                        requestedQty,
                        uom,
                        fill,
                        status
                    )
                    listOfSourceAndSite.add(sourceOrSite)
                }
            }
        }

        var truck = TruckInfo(it.truck.truckId, it.truck.truckCode, it.truck.truckDesc)
        var trip = Trip(
            truck,
            it.tripInfo.tripId,
            it.tripInfo.tripName,
            it.tripInfo.tripDate,
            listOfSourceAndSite,
            it.tripInfo.status
        )
        finalList.add(trip)

    }

    return finalList
}


fun List<DatabaseTripsWithInfo>.asNetworkModel(): List<NetworkTrip> {

    var finalList = mutableListOf<NetworkTrip>()

    map {
        for (each in it.sourceOrSite) {
            each.source.apply {
                var individualInfo = NetworkTrip(
                    it.truck.truckId,
                    it.truck.truckCode,
                    it.truck.truckDesc,
                    each.trailer.trailerId,
                    each.trailer.trailerCode,
                    each.trailer.trailerDesc,
                    it.tripInfo.tripId,
                    it.tripInfo.tripName,
                    it.tripInfo.tripDate,
                    seqNum,
                    wayPointTypeDescription,
                    latitude,
                    longitude,
                    destinationCode,
                    destinationName,
                    address1,
                    city,
                    stateAbbrev,
                    postalCode,
                    siteContainerCode,
                    siteContainerDescription,
                    delReqNum,
                    delReqLineNum,
                    productId,
                    productCode,
                    productDesc,
                    requestedQty,
                    uom,
                    fill
                )

                finalList.add(individualInfo)
            }
        }

    }

    return finalList
}