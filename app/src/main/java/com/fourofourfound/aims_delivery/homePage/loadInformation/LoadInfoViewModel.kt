package com.fourofourfound.aims_delivery.homePage.loadInformation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.repository.TripListRepository
import com.fourofourfound.aims_delivery.utils.StatusEnum
import com.fourofourfound.aims_delivery.utils.getDatabaseForDriver
import kotlinx.coroutines.launch

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
    fun changeTripStatus(tripId: Int, status: StatusEnum) {
        viewModelScope.launch {
            //TODO change status of trips
            tripListRepository.changeTripStatus(tripId, status)
        }
    }



    fun changeDeliveryStatus(tripId: Int, seqNum: Int, status: StatusEnum) {
        viewModelScope.launch {
            //TODO change status of trips
            tripListRepository.updateDeliveryStatus(tripId, seqNum, status)
        }
    }

    fun sendTripSelectedData(){
        sendTripData.value = true
    }

    fun sendDestinationSelectedData() {

    }


}
