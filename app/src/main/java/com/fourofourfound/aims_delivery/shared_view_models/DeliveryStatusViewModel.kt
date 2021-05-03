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
 *
 * @constructor
 *
 * @param application the applicationContext which created this viewModel
 */
class DeliveryStatusViewModel(application: Application) : AndroidViewModel(application) {
    private val myApplication = application
    var database = getDatabaseForDriver(application)

    fun getUpdatedDatabase() {
        database = getDatabaseForDriver(myApplication)
    }


    companion object {
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
                        Log.i("NETWORK-CALL", " successful for $toPut")


                    } catch (ex: Exception) {
                        database.statusPutDao.insertPutData(toPut)
                        Log.i("NETWORK-CALL", "failed")
                    }
                }

            }
        }

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


    fun sendProductPickedUp(
        toSend: ProductPickedUpData,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO)
            {
                try {
                    toSend.apply {
//                        MakeNetworkCall.retrofitService.sendProductPickupInfo(
//                            driverCode,
//                            tripId,
//                           sourceId,productId,billOfLadingNumber,startTime,endTime,grossQty,netQty
//                        )

                        database.completedDeliveriesDao.updateFormSentStatus(sourceId)
                    }

                } catch (ex: HttpException) {

                } catch (ex: Exception) {

                }

            }

        }
    }


    var previousDestination: SourceOrSite? = null
    val locationRepository = LocationRepository(application)

    var destinationApproachingShown = false
    var destinationLeavingShown = false

}