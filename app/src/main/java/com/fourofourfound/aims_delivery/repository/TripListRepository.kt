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
import com.fourofourfound.aims_delivery.network.asFiltered
import com.fourofourfound.aims_delivery.utils.DeliveryStatusEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Trip list repository
 * This class holds the trip information and allow access to database
 * @property database the database to be used
 * @constructor
 */
class TripListRepository(private val database: TripListDatabase) {

    /**
     * Trips
     * This helps to convert the data retrieved from database to live data of domain model.
     */
    val trips = Transformations.map(database.tripDao.getAllTrip())
    {
        it.asDomainModel()
    }

    /**
     * Updating trips
     * Flag for updating trips.
     */
    var updatingTrips = false

    /**
     * Refresh trips
     * Refresh the trips stored in the offline cache.
     */
    suspend fun refreshTrips(code: String) {
        withContext(Dispatchers.IO) {
            try {
                val networkTrips = MakeNetworkCall.retrofitService.getAllTrips(code).data.resultSet1
                val filteredNetworkList = networkTrips.map { it.asFiltered() }
                val databaseData = database.tripDao.getAllTripsOneTime()
                val storedData = databaseData.asNetworkModel()

                //If there are new trips from the network, save it to the database.
                if (!storedData.containsAll(filteredNetworkList)) {
                    updatingTrips = true
                    val removed = (storedData.filterNot { filteredNetworkList.contains(it) })
                    deleteTrips(removed.map { it.tripId as Int })
                    saveTrips(filteredNetworkList)
                } else {
                }
            } catch (e: Exception) {
                Log.i("ERROR!!!", e.stackTraceToString())
            }
        }

    }

    /**
     * Save trips
     * This method saves the trip to the database.
     * @param list the list of network trips to be saved to the database.
     */
    private suspend fun saveTrips(list: List<NetworkTrip>) {
        withContext(Dispatchers.IO) {
            try {
                for (each in list) {
                    each.asFiltered().apply {
                        val savedTrip = database.tripDao.getTripById(tripId!!)
                        val trip = DatabaseTrip(tripId, tripName!!, tripDate!!)
                        savedTrip?.apply { trip.deliveryStatus = deliveryStatus }
                        val truck = DatabaseTruck(truckId!!, truckCode!!, truckDesc!!)
                        val trailer = DatabaseTrailer(trailerId!!, trailerCode!!, trailerDesc!!)
                        val fuel = DatabaseFuel(productId!!, productCode, productDesc)
                        val location = DatabaseLocation(
                            address1!!,
                            address2,
                            city!!,
                            stateAbbrev!!,
                            postalCode!!,
                            latitude!!,
                            longitude!!,
                            destinationCode!!,
                            destinationName!!,
                            "$tripId $seqNum"
                        )
                        val sourceOrSite = DatabaseSourceOrSite(
                            tripId,
                            truckId,
                            trailerId,
                            productId,
                            destinationCode,
                            seqNum!!,
                            waypointTypeDescription!!,
                            siteContainerCode,
                            siteContainerDescription,
                            delReqNum,
                            delReqLineNum,
                            requestedQty!!,
                            uom!!,
                            fill!!, sourceId, siteId
                        )

                        var savedSourceOrSite =
                            database.destinationDao.getDestination(tripId, seqNum)
                        savedSourceOrSite?.apply {
                            sourceOrSite.deliveryStatus = savedSourceOrSite.deliveryStatus
                        }

                        database.locationDao.insertLocation(location)
                        database.tripDao.insertTruck(truck)
                        database.trailerDao.insertTrailer(trailer)
                        database.tripDao.insertTrip(trip)
                        database.productsDao.insertFuel(fuel)
                        database.destinationDao.insertDestination(sourceOrSite)
                    }
                }
            } catch (e: Exception) {
                Log.i("ERROR!!!", e.stackTraceToString())

            } finally {
                updatingTrips = false
            }
        }
    }

    /**
     * Mark Trip Completed
     * Marks the trip as completed when the trip finishes.
     * @param tripId The id of the trip
     * @param deliveryStatus The status of the trip
     */
    suspend fun changeTripStatus(tripId: Int, deliveryStatus: DeliveryStatusEnum) {
        withContext(Dispatchers.IO) {
            try {
                //TODO make network call to inform aims dispatcher
                database.tripDao.changeTripStatus(tripId, deliveryStatus)
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

    /**
     * Send form data
     * This method inserts the form data to the database.
     * @param formToSubmit the filled up form to be saved in the database.
     */
    suspend fun sendFormData(formToSubmit: DatabaseCompletionForm) {
        withContext(Dispatchers.IO) {
            try {
                database.formDao.insertFormData(formToSubmit)
            } catch (e: Exception) {
                Log.i("ERROR!!!", e.stackTraceToString())
                try {
                    database.formDao.insertFormData(formToSubmit)
                } catch (e: Exception) {
                    Log.i("ERROR!!!", e.toString())
                }
            }
        }
    }

    /**
     * Update delivery status
     * This method updates the delivery status of the given trip id according to
     * the sequence number of the delivery.
     * @param tripId the id of the ongoing trip
     * @param seqNum the sequence number of on going delivery
     * @param deliveryStatus the status of the delivery (ongoing, completed or not started)
     */
    suspend fun updateDeliveryStatus(tripId: Int, seqNum: Int, deliveryStatus: DeliveryStatusEnum) {
        try {
            database.destinationDao.updateDeliveryStatus(tripId, seqNum, deliveryStatus)
        } catch (e: Exception) {
        }
    }

    /**
     * Update trailer fuel
     * This method updates the trailer fuel amount of the given trailer id.
     * @param trailerId the id of the trailer of the truck
     * @param fuelQuantity the quantity of the fuel to be updated
     */
    suspend fun updateTrailerFuel(trailerId: Int, fuelQuantity: Double) {
        try {
            database.trailerDao.updateTrailerFuel(trailerId, fuelQuantity)
        } catch (e: Exception) {
        }
    }

    /**
     * Delete trips
     * This method delete the trip with the given trip id from the database.
     * @param ids the id of the trip to be deleted
     */
    private fun deleteTrips(ids: List<Int>) {
        val completedTrips = database.tripDao.getCompletedTripsId()
        ids.minus(completedTrips)
        database.tripDao.deleteTripById(ids)
    }

    /**
     * Add bill of lading images
     * This method adds the path of the image to the database.
     * @param tripId the id of the current trip
     * @param seqNum the seq number of ongoing delivery
     * @param imagePaths the path of the image
     */
    fun addBillOfLadingImages(tripId: Int, seqNum: Int, imagePaths: MutableList<String>?) {
        if (imagePaths != null) {
            var billOfLadingImages = mutableListOf<BillOfLadingImages>()
            for (image in imagePaths) {
                billOfLadingImages.add(BillOfLadingImages("$tripId $seqNum", image))
            }
            database.formDao.addBillOfLadingImages(billOfLadingImages)
        }
    }

    /**
     * Get total trips completed
     * This methods give the total number of completed trips.
     * @return the total number of trips completed
     */
    suspend fun getTotalTripsCompleted(): Int {
        var totalCompleted = 0
        withContext(Dispatchers.IO) {
            totalCompleted = database.tripDao.getTotalCompletedTrips()
        }
        return totalCompleted
    }

    /**
     * Get total deliveries made
     * This method gives the total number of deliveries in all trips.
     * @return total number of deliveries made
     */
    suspend fun getTotalDeliveriesMade(): Int {
        var totalCompleted: Int
        withContext(Dispatchers.IO) {
            totalCompleted = database.destinationDao.getTotalDestinations()
        }
        return totalCompleted
    }
}