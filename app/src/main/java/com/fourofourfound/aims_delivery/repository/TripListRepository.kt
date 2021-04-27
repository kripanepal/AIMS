package com.fourofourfound.aims_delivery.repository

import android.util.Log
import androidx.lifecycle.Transformations
import com.fourofourfound.aims_delivery.database.TripListDatabase
import com.fourofourfound.aims_delivery.database.entities.*
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.database.relations.asDomainModel
import com.fourofourfound.aims_delivery.database.relations.asNetworkModel
import com.fourofourfound.aims_delivery.network.MakeNetworkCall
import com.fourofourfound.aims_delivery.network.NetworkTrip
import com.fourofourfound.aims_delivery.utils.StatusEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Trip list repository
 * This class holds the trip information and allow access to database
 * @property database the database to be used
 * @constructor Create empty Trip list repository
 */
class TripListRepository(private val database: TripListDatabase) {
    private val tripsFromDatabase = database.tripDao.getAllTrip()
    val trips = Transformations.map(tripsFromDatabase)
    {
        it.asDomainModel()
    }

    private var driverCode: String = ""
    private var driverName: String = ""
    /**
     * Refresh trips
     * Refresh the trips stored in the offline cache.
     */
    suspend fun refreshTrips() {
        withContext(Dispatchers.IO) {
            try {
                val tripLists = MakeNetworkCall.retrofitService.getAllTrips().data.resultSet1
                driverName = tripLists[0].driverName
                driverCode = tripLists[0].driverCode


                    saveTrips(tripLists)

            } catch (e: Exception) {
            //todo need to do actual error handling
                Log.i("drivername", e.toString())
            }
        }
    }

    private suspend fun saveTrips(list: List<NetworkTrip>) {
        withContext(Dispatchers.IO) {
            try {
                for (each in list) {
                    each.apply {
                        var savedTrip = database.tripDao.getTripById(tripId)
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
                        var driver = Driver(driverCode, driverName)

                        var savedSourceOrSite =
                            database.destinationDao.getDestination(tripId, seqNum)
                        savedSourceOrSite?.apply { sourceOrSite.status = savedSourceOrSite.status }

                        //todo delete all records before adding after if, not here
                        database.tripDao.insertTruck(truck)
                        database.trailerDao.insertTrailer(trailer)
                        database.tripDao.insertTrip(trip)
                        database.productsDao.insertFuel(fuel)
                        database.tripDao.insertLocation(location)
                        database.destinationDao.insertDestination(sourceOrSite)
                        database.driverDao.insertDriver(driver)
                    }
                }
            } catch (e: Exception) {
                Log.i("DatabaseError", e.stackTraceToString())

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
    suspend fun changeTripStatus(tripId: Int, status: StatusEnum) {
        withContext(Dispatchers.IO) {
            try {
                //TODO make network call to inform aims dispatcher
                database.tripDao.changeTripStatus(tripId, status)
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
                database.tripDao.deleteAllTrips()
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
                database.locationDao.insertLocation(customLocation)
            } catch (e: Exception) {
            }
        }
    }

    suspend fun sendFormData(formToSubmit: DatabaseCompletionForm) {
        withContext(Dispatchers.IO) {
            try {
                MakeNetworkCall.retrofitService.sendFormData(formToSubmit)

            } catch (e: Exception) {
                try {
                    database.formDao.insertFormData(formToSubmit)
                } catch (e: Exception) {
                }
            }

        }
    }

    suspend fun updateDeliveryStatus(tripId: Int, seqNum: Int, status: StatusEnum) {
        try {
            database.destinationDao.updateDeliveryStatus(tripId, seqNum, status)
        } catch (e: Exception) {
        }
    }

    suspend fun updateTrailerFuel(trailerId: Int, fuelQuantity: Double) {
        try {
            database.trailerDao.updateTrailerFuel(trailerId, fuelQuantity)
        } catch (e: Exception) {
        }
    }


}