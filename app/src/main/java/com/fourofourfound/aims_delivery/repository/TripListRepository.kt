package com.fourofourfound.aims_delivery.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.fourofourfound.aims_delivery.database.TripListDatabse
import com.fourofourfound.aims_delivery.database.entities.DatabaseSourceOrSite
import com.fourofourfound.aims_delivery.database.entities.DatabaseTrailer
import com.fourofourfound.aims_delivery.database.entities.DatabaseTrip
import com.fourofourfound.aims_delivery.database.entities.DatabaseTruck
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.network.MakeNetworkCall
import com.fourofourfound.aims_delivery.network.NetworkModels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Trip list repository
 * This class holds the trip information and allow access to database
 * @property database the database to be used
 * @constructor Create empty Trip list repository
 */
class TripListRepository(private val database: TripListDatabse) {

    val updating = MutableLiveData(false)
    val trips: LiveData<List<Trip>>? = Transformations.map(database.tripListDao.getAllTrip()) {
        null
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
                Log.i("AAAAAA", tripLists.data.resultSet1.toString())

            } catch (e: Exception) {
                //TODO check ID spelling on actual JSON
                Log.i("AAAAAAAAAAAAA", e.message.toString())
            }


        }
    }

    suspend fun saveTrips(list: List<NetworkModels.NetworkTrip>) {


        withContext(Dispatchers.IO) {

            try {
                for (each in list) {
                    each.apply {
                        var truck = DatabaseTruck(truckId, truckCode, truckDesc, tripId)
                        var trailer = DatabaseTrailer(trailerId, trailerCode, trailerDesc, truckId)
                        var sourceOrSite = DatabaseSourceOrSite(
                            tripId,
                            seqNum,
                            waypointTypeDescription,
                            latitude,
                            longitude,
                            destinationCode,
                            destinationName,
                            siteContainerCode,
                            siteContainerDescription,
                            address1,
                            city,
                            stateAbbrev,
                            postalCode,
                            delReqNum,
                            delReqLineNum,
                            productId,
                            productCode,
                            productDesc,
                            requestedQty,
                            uom,
                            fill,
                            truckId,
                            trailerId
                        )
                        var trip = DatabaseTrip(tripId, tripName, tripDate)

                        database.tripListDao.insertTruck(truck)
                        database.tripListDao.insertTrailer(trailer)
                        database.tripListDao.insertSitesOrSource(sourceOrSite)
                        database.tripListDao.insertTrip(trip)
                    }

                }
            } catch (e: Exception) {
                //TODO check ID spelling on actual JSON
                Log.i("AAAAAAAAAAAAA", e.message.toString())
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
    suspend fun changeTripStatus(tripId: String, status: String) {
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
}