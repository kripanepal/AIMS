package com.fourofourfound.aims_delivery.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.fourofourfound.aims_delivery.database.TripListDatabse
import com.fourofourfound.aims_delivery.database.entities.asDomainModel
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.network.tripList.asDatabaseModel
import com.fourofourfound.aims_delivery.network.user.MakeNetworkCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TripListRepository(private val database: TripListDatabse) {

    /**
     * A list of trips that can be shown on the screen.
     */
    val trips: LiveData<List<Trip>> =
        Transformations.map(database.tripListDao.getTripList()) {
            it.asDomainModel()
        }

    /**
     * Refresh the trips stored in the offline cache.

     */
    suspend fun refreshTrips() {
        withContext(Dispatchers.IO) {
            try {
                val tripLists = MakeNetworkCall.retrofitService.getAllTrips()
                database.tripListDao.insertAll(*tripLists.asDatabaseModel())
            } catch (e: Exception) {

            }
        }
    }

    suspend fun markTripCompleted(tripId:String,status:Boolean)
    {
        withContext(Dispatchers.IO) {
            try {
               //TODO make network call to inform aims dispatcher
                database.tripListDao.markTripCompleted(tripId,status)
            } catch (e: Exception) {

            }

        }
    }
}