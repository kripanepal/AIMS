package com.fourofourfound.aims_delivery.repository

import android.util.Log
import androidx.lifecycle.Transformations
import com.fourofourfound.aims_delivery.database.TripListDatabse
import com.fourofourfound.aims_delivery.database.entities.*
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.database.relations.asDomainModel
import com.fourofourfound.aims_delivery.network.MakeNetworkCall
import com.fourofourfound.aims_delivery.network.NetworkTrip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Trip list repository
 * This class holds the trip information and allow access to database
 * @property database the database to be used
 * @constructor Create empty Trip list repository
 */
class TripListRepository(private val database: TripListDatabse) {


    private val tripsFromDatabase = database.tripListDao.getAllTrip()
    val trips = Transformations.map(tripsFromDatabase)
    {
        it.asDomainModel()
    }


    /**
     * Refresh trips
     * Refresh the trips stored in the offline cache.
     */
    suspend fun refreshTrips() {
        withContext(Dispatchers.IO) {
            try {
                val tripLists = MakeNetworkCall.retrofitService.getAllTrips()
                saveTrips(tripLists.data.resultSet1)

            } catch (e: Exception) { //todo need to do actual error handling
            }
        }
    }

    private suspend fun saveTrips(list: List<NetworkTrip>) {
        withContext(Dispatchers.IO) {
            try {
                for (each in list) {
                    each.apply {
                        var savedTrip = database.tripListDao.getTripById(tripId)
                        var trip = DatabaseTrip(tripId, tripName, tripDate)
                        savedTrip?.apply { trip.status = status }

                        var truck = DatabaseTruck(truckId, truckCode, truckDesc)
                        var trailer = DatabaseTrailer(trailerId, trailerCode, trailerDesc)
                        var fuel = DatabaseFuel(productId, productCode, productDesc)
                        var location = DatabaseLocation(
                            address1,
                            address2,
                            city,
                            stateAbbrev,
                            postalCode,
                            latitude,
                            longitude,
                            destinationCode,
                            destinationName
                        )
                        var sourceOrSite = DatabaseSourceOrSite(
                            tripId,
                            truckId,
                            trailerId,
                            productId,
                            destinationCode,
                            seqNum,
                            waypointTypeDescription,
                            siteContainerCode,
                            siteContainerDescription,
                            delReqNum,
                            delReqLineNum,
                            requestedQty,
                            uom,
                            fill
                        )


                        var savedSourceOrSite = database.tripListDao.getSourceOrSite(tripId, seqNum)
                        savedSourceOrSite?.apply { sourceOrSite.status = savedSourceOrSite.status }

                        //todo delete all records before adding after if, not here
                        database.tripListDao.insertTruck(truck)
                        database.tripListDao.insertTrailer(trailer)
                        database.tripListDao.insertTrip(trip)
                        database.tripListDao.insertFuel(fuel)
                        database.tripListDao.insertLocation(location)
                        database.tripListDao.insertSitesOrSource(sourceOrSite)


//                        trip = DatabaseTrip(160, "A-160", "DATE")
//                        location = DatabaseLocation(
//                            "ADDR!",
//                            "addr2",
//                            "Monroe",
//                            "LA",
//                            71203,
//                            94.12,
//                            -112.15,
//                            "DESTCODE",
//                            "DESTDESC"
//                        )
//                        sourceOrSite = DatabaseSourceOrSite(
//                            160,
//                            4,
//                            3,
//                            759,
//                            "DESTCODE",
//                            0,
//                            "Source",
//                            "SITE CODE",
//                            "SITE DESC",
//                            20,
//                            20,
//                            5000,
//                            "GAL",
//                            "FILL"
//                        )
//
//                        database.tripListDao.insertTrip(trip)
//                        database.tripListDao.insertLocation(location)
//                        database.tripListDao.insertSitesOrSource(sourceOrSite)
                    }
                }
            } catch (e: Exception) {
                Log.i("AAAAAAAAAA", e.stackTraceToString())

            }
        }


    }

    /**
     * Mark Trip Completed
     * Marks the trip as completed when the trip
     * finishes.
     * @param tripId The id of the trip
     * @param status The status of the trip
     */
    suspend fun changeTripStatus(tripId: Int, status: String) {
        withContext(Dispatchers.IO) {
            try {
                //TODO make network call to inform aims dispatcher
                database.tripListDao.changeTripStatus(tripId, status)
            } catch (e: Exception) {

            }

        }
    }

    /**
     * Delete all trips
     * Deletes all the trips from the local database
     */
    suspend fun deleteAllTrips() {
        withContext(Dispatchers.IO) {
            try {
                //TODO make network call to inform aims dispatcher
                database.tripListDao.deleteAllTrips()
            } catch (e: Exception) {

            }
        }
    }

    /**
     * Save Location to Database
     * Saves the user current location to the local database
     * @param customLocation Current location of the user
     */
    suspend fun saveLocationToDatabase(customLocation: CustomDatabaseLocation) {
        withContext(Dispatchers.IO) {
            try {
                database.tripListDao.insertLocation(customLocation)
            } catch (e: Exception) {
            }
        }
    }

    suspend fun sendFormData(formToSubmit: DatabaseForm) {
        withContext(Dispatchers.IO) {
            try {
                MakeNetworkCall.retrofitService.sendFormData(formToSubmit)

            } catch (e: Exception) {
                try {
                    database.tripListDao.insertFormData(formToSubmit)
                } catch (e: Exception) {
                }
            }


        }
    }

    fun markDeliveryCompleted(tripId: Int, seqNum: Int) {
        try {
            database.tripListDao.markDeliveryCompleted(tripId, seqNum)
        } catch (e: Exception) {
        }
    }
}
