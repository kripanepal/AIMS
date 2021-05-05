package com.fourofourfound.aims_delivery.shared_view_models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.fourofourfound.aims_delivery.database.TripListDatabase
import com.fourofourfound.aims_delivery.database.entities.DatabaseStatusPut
import com.fourofourfound.aims_delivery.database.utilClasses.ProductData
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.network.MakeNetworkCall
import com.fourofourfound.aims_delivery.network.ProductPickupResponse
import com.fourofourfound.aims_delivery.network.StatusMessageUpdateResponse
import com.fourofourfound.aims_delivery.repository.LocationRepository
import com.fourofourfound.aims_delivery.utils.getDatabaseForDriver
import com.fourofourfound.aims_delivery.utils.getDateAndTime
import kotlinx.coroutines.*
import retrofit2.HttpException


/**
 * Shared View Model
 * This view model stores the information about current user, connection status and
 * currently selected trip. This View Model is used buy multiple fragments
 * @constructor
 *
 * @param application the applicationContext which created this viewModel
 */
class DeliveryStatusViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * My application
     * Base class for maintaining global application state
     */
    private val myApplication = application

    /**
     * Database
     * The database for the driver that is used by the view model
     */
    var database = getDatabaseForDriver(application)

    /**
     * Get updated database
     * This method sets the value of database for new driver if the driver is changed
     */
    fun getUpdatedDatabase() {
        database = getDatabaseForDriver(myApplication)
    }


    companion object {
        /**
         * Send status update
         * THis method makes network call to send status update with required information
         * and saves the information to database if the update fails
         * @param toPut the update that is to be sent
         * @param database the database where the information is stored if the update fails
         */
        fun sendStatusUpdate(
            toPut: DatabaseStatusPut, database: TripListDatabase
        ) {
            GlobalScope.launch {
                withContext(Dispatchers.IO)
                {
                    try {
                        toPut.apply {
                            Log.i("NETWORK-CALL", "\nSending  $statusMessage message")
                            MakeNetworkCall.retrofitService.sendStatusUpdate(
                                driverCode,
                                tripId,
                                statusCode,
                                statusMessage,
                                statusDate
                            )
                        }
                        database.statusPutDao.deletePutData(toPut)
                        Log.i("NETWORK-CALL", " successful for $toPut")
                    } catch (e: Exception) {
                        //Save to local database if the update fails
                        database.statusPutDao.insertPutData(toPut)
                        Log.i("NETWORK-CALL", e.message.toString())
                    }
                }

            }
        }


        suspend fun sendUnsentPickupMessages(database: TripListDatabase) {
            val unsentPickupList = database.completedDeliveriesDao.getUnsentFormInfo()
            coroutineScope {
                unsentPickupList.map {
                    async(Dispatchers.IO) {

                        sendProductFulfilledMessage(it, database)

                    }
                }.awaitAll()
            }

        }


        suspend fun sendUnsentPutMessages(database: TripListDatabase) {
            val statusPutToSend: List<DatabaseStatusPut> = database.statusPutDao.getAllUnsentData()

            coroutineScope {
                statusPutToSend.map {
                    async(Dispatchers.IO) {
                        sendStatusUpdate(it, database)
                    }
                }.awaitAll()
            }
        }

        suspend fun sendUnsentLocation(database: TripListDatabase) {
            try {
                //get saved location from the database
                val locationToSend = database.locationDao.getSavedLocation()
                //send the data to the dispatcher
                MakeNetworkCall.retrofitService.sendLocation(locationToSend)

                //delete contents of location table as only latest location is required
                database.locationDao.deleteAllLocations()
            } catch (e: Exception) {
            }
        }


        /**
         * Send product picked up message
         * THis method makes network call to send product picked up message with required information
         * and saves the information to database if the update fails
         * @param productInfo the product information that needs to be sent
         * @param database the database where the information is stored if the update fails
         */
        fun sendProductFulfilledMessage(
            productInfo: ProductData,
            database: TripListDatabase
        ) {
            GlobalScope.launch {
                withContext(Dispatchers.IO)
                {
                    try {
                        val response = productInfo.run {
                            Log.i("NETWORK-CALL", "sending message for $type")
                            if (type == "Source")
                                MakeNetworkCall.retrofitService.sendProductPickupInfo(
                                    driverCode,
                                    tripId,
                                    sourceOrSiteId,
                                    productId,
                                    billOfLadingNumber,
                                    getDateAndTime(startTime),
                                    getDateAndTime(endTime),
                                    grossQty,
                                    netQty
                                )
                            else MakeNetworkCall.retrofitService.sendProductDropOffInfo(
                                driverCode,
                                tripId,
                                sourceOrSiteId,
                                productId,
                                getDateAndTime(endTime),
                                grossQty.toString(),
                                netQty.toString(),
                                trailerRemainingQuantity.toString()
                            )
                        }
                        var successFull = false
                        if (response.javaClass == ProductPickupResponse::class.java && (response as ProductPickupResponse).status == "success") successFull =
                            true
                        if (response.javaClass == StatusMessageUpdateResponse::class.java && (response as StatusMessageUpdateResponse).status == "success") successFull =
                            true


                        if (successFull) {
                            Log.i("NETWORK-CALL", "Successful for ${productInfo.type}  $productInfo")
                            database.completedDeliveriesDao.updateFormSentStatus(
                                (productInfo.sourceOrSiteId)
                            )
                        }
                        else  Log.i("NETWORK-CALL", "Failed")
                    } catch (e: HttpException) {
                        Log.i("NETWORK-CALL", "Failed")
                    } catch (e: Exception) {
                        Log.i("NETWORK-CALL", "Failed")

                    }
                }
            }
        }
    }

    var previousDestination: SourceOrSite? = null
    val locationRepository = LocationRepository(application)
    var destinationApproachingShown = false
    var destinationLeavingShown = false

}