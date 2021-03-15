package com.fourofourfound.aims_delivery.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.fourofourfound.aims_delivery.database.TripListDatabse
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.database.relations.DatabaseTripsWithInfo
import com.fourofourfound.aims_delivery.domain.asDatabaseModel
import com.fourofourfound.aims_delivery.domain.asDomainModel
import com.fourofourfound.aims_delivery.network.MakeNetworkCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Trip list repository
 * This class holds the trip information and allow access to database
 * @property database the database to be used
 * @constructor Create empty Trip list repository
 */
class TripListRepository(private val database: TripListDatabse) {


    val trips: LiveData<List<DatabaseTripsWithInfo>> = database.tripListDao.getAllTrip()


    /**
     * Refresh trips
     * Refresh the trips stored in the offline cache.
     */
    suspend fun refreshTrips() {

        withContext(Dispatchers.IO) {
            try {
                val tripLists = MakeNetworkCall.retrofitService.getAllTrips()
                for (each in tripLists) {
                    database.tripListDao.apply {
                        insertTrip(each.asDatabaseModel())
                        insertSources(each.source.asDatabaseModel(each.tripID))
                        insertSites(each.site.asDomainModel(each.tripID))

                        for (source in each.source) {
                            insertFuel(source.fuel.asDatabaseModel(source.sourceID))
                            insertLocation(source.location.asDatabaseModel(source.sourceID))
                        }

                        for (site in each.site) {
                            insertFuel(site.fuel.asDatabaseModel(site.siteID))
                            insertLocation(site.location.asDatabaseModel(site.siteID))
                        }

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