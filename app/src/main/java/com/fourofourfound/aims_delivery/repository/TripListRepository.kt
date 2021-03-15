package com.fourofourfound.aims_delivery.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.fourofourfound.aims_delivery.database.TripListDatabse
import com.fourofourfound.aims_delivery.database.entities.asDomainModal
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.network.MakeNetworkCall
import com.fourofourfound.aims_delivery.network.tripList.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Trip list repository
 * This class holds the trip information and allow access to database
 * @property database the database to be used
 * @constructor Create empty Trip list repository
 */
class TripListRepository(private val database: TripListDatabse) {

    /**
     * Trips
     * A list of trips that can be shown on the screen.
     */
    val trips: LiveData<List<Trip>> =
        Transformations.map(database.tripListDao.getTripList()) {
            it.asDomainModal()
        }


    /**
     * Refresh trips
     * Refresh the trips stored in the offline cache.
     */
    suspend fun refreshTrips() {

        withContext(Dispatchers.IO) {
            try {
                val tripLists = MakeNetworkCall.retrofitService.getAllTrips()
                database.tripListDao.insertTrips(*tripLists.asDatabaseModel())
            } catch (e: Exception) {

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
    suspend fun markTripCompleted(tripId: String, status: Boolean) {
        withContext(Dispatchers.IO) {
            try {
                //TODO make network call to inform aims dispatcher
                database.tripListDao.markTripCompleted(tripId, status)
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