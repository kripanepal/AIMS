package com.fourofourfound.aims_delivery.shared_view_models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.database.TripListDatabase
import com.fourofourfound.aims_delivery.database.entities.DatabaseStatusPut
import com.fourofourfound.aims_delivery.database.utilClasses.ProductPickedUpData
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.network.MakeNetworkCall
import com.fourofourfound.aims_delivery.repository.LocationRepository
import com.fourofourfound.aims_delivery.utils.getDatabaseForDriver
import com.fourofourfound.aims_delivery.utils.getDateAndTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                    } catch (ex: Exception) {
                        //Save to local database if the update fails
                        database.statusPutDao.insertPutData(toPut)
                        Log.i("NETWORK-CALL", "failed")
                    }
                }

            }
        }

        /**
         * Send product picked up message
         * THis method makes network call to send product picked up message with required information
         * and saves the information to database if the update fails
         * @param pickupInfo the product information that needs to be sent
         * @param database the database where the information is stored if the update fails
         */
        fun sendProductPickedUpMessage(
            pickupInfo: ProductPickedUpData,
            database: TripListDatabase
        ) {
            GlobalScope.launch {
                withContext(Dispatchers.IO)
                {
                    try {
                        Log.i("NETWORK-CALL", "\nsending saved product pickup message")
                        val response = pickupInfo.run {
                            MakeNetworkCall.retrofitService.sendProductPickupInfo(
                                driverCode,
                                tripId,
                                sourceId,
                                productId,
                                billOfLadingNumber,
                                getDateAndTime(startTime),
                                getDateAndTime(endTime),
                                grossQty,
                                netQty
                            )
                        }
                        //Save to local database if update fails
                        if (response.status == "success") {
                            Log.i("NETWORK-CALL", "Successful for  $pickupInfo")
                            database.completedDeliveriesDao.updateFormSentStatus(
                                (pickupInfo.sourceId)
                            )
                        }
                    } catch (e: HttpException) {
                        Log.i("NETWORK-CALL", e.stackTraceToString())
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