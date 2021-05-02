package com.fourofourfound.aims_delivery.homePage.loadInformation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.network.MakeNetworkCall
import com.fourofourfound.aims_delivery.repository.TripListRepository
import com.fourofourfound.aims_delivery.utils.DeliveryStatusEnum
import com.fourofourfound.aims_delivery.utils.getDatabaseForDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.lang.Exception

/**
 * Home page view model
 *This view model is used by the HomePageFragment to store the information about all trips
 * @constructor
 *
 * @param application the applicationContext which created this viewModel
 */
class LoadInfoViewModel(application: Application) : AndroidViewModel(application) {

    val database = getDatabaseForDriver(application)
    private val tripListRepository = TripListRepository(database)
    //TODO NEED TO REMOVE THESE
    val sendTripData = MutableLiveData(false)

    /**
     * Mark trip as completed
     *This function is called whenever the user marks any trip as completed
     * Local database is updated to reflect this change
     */
    fun changeTripStatus(tripId:Int, deliveryStatus: DeliveryStatusEnum) {
        viewModelScope.launch {
       //change in local database
            tripListRepository.changeTripStatus(tripId, deliveryStatus)
        }
    }

    fun sendStatusUpdate(driverCode:String,
                         tripId:Int,
                         statusCode:String,
                         statusMessage:String,
                         statusDate:String) {
        viewModelScope.launch {
          withContext(Dispatchers.IO)
          {
              try {
                   MakeNetworkCall.retrofitService.sendStatusUpdate(driverCode,tripId,statusCode,statusMessage, statusDate)
              }

              catch (ex:HttpException)
              {
              }
              catch (ex:Exception)
              {
              }

          }

        }
    }


    fun changeDeliveryStatus(tripId: Int, seqNum: Int, deliveryStatus: DeliveryStatusEnum) {
        viewModelScope.launch {
            //TODO change status of trips
            tripListRepository.updateDeliveryStatus(tripId, seqNum, deliveryStatus)
        }
    }

    fun sendTripSelectedData(){
        sendTripData.value = true
    }



}
