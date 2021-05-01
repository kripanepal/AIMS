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

                val storedData = tripsFromDatabase.value?.asNetworkModel()!!
                if (!storedData.containsAll(filteredNetworkList)) {
                    val removed = storedData.subtract(filteredNetworkList).map {
                        it.tripId as Int
                    }
                    deleteTrips(removed)
                    updatingTrips = true
                    saveTrips(filteredNetworkList)
                } else {
                }
            } catch (e: Exception) {
                Log.i("ERROR!!!", e.stackTraceToString())
            }
        }

    }

    private suspend fun saveTrips(list: List<NetworkTrip>) {

        withContext(Dispatchers.IO) {
            try {
                for (each in list) {
                    each.asFiltered().apply {
                        val savedTrip = database.tripDao.getTripById(tripId!!)
                        val trip = DatabaseTrip(tripId, tripName!!, tripDate!!)
                        savedTrip?.apply { trip.status = status }

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
                            fill!!
                        )
                        var driver = Driver(driverCode!!, driverName!!)

                        var savedSourceOrSite =
                            database.destinationDao.getDestination(tripId, seqNum)
                        savedSourceOrSite?.apply { sourceOrSite.status = savedSourceOrSite.status }

                        //todo delete all records before adding after if, not here
                        database.locationDao.insertLocation(location)
                        database.tripDao.insertTruck(truck)
                        database.trailerDao.insertTrailer(trailer)
                        database.tripDao.insertTrip(trip)
                        database.productsDao.insertFuel(fuel)
                        database.destinationDao.insertDestination(sourceOrSite)
                        database.driverDao.insertDriver(driver)
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

    private fun deleteTrips(ids: List<Int>) {

        database.tripDao.deleteTripById(ids)

    }

    fun addBillOfLadingImages(tripId: Int, seqNum: Int, imagePaths: MutableList<String>?) {
        if (imagePaths != null) {
            var billOfLadingImages = mutableListOf<BillOfLadingImages>()
            for (image in imagePaths) {
                billOfLadingImages.add(BillOfLadingImages("$tripId $seqNum", image))
            }
            database.formDao.addBillOfLadingImages(billOfLadingImages)
        }

    }


}