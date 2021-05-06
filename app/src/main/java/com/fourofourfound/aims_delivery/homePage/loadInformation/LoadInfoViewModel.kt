package com.fourofourfound.aims_delivery.homePage.loadInformation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.repository.TripListRepository
import com.fourofourfound.aims_delivery.utils.DeliveryStatusEnum
import com.fourofourfound.aims_delivery.utils.getDatabaseForDriver
import kotlinx.coroutines.launch

/**
 * Home page view model
 *This view model is used by the HomePageFragment to store the information about all trips
 * @constructor
 * @param application the applicationContext which created this viewModel
 */
class LoadInfoViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Database
     * The database for the currently logged in driver
     */
    val database = getDatabaseForDriver(application)

    /**
     * Trip list repository
     * The repository that holds the information about the trip and the destination
     */
    private val tripListRepository = TripListRepository(database)

    /**
     * Mark trip as completed
     *This function is called whenever the user marks any trip as completed
     * Local database is updated to reflect this change
     */
    fun changeTripStatus(tripId: Int, deliveryStatus: DeliveryStatusEnum) {
        viewModelScope.launch {
            //change in local database
            tripListRepository.changeTripStatus(tripId, deliveryStatus)
        }
    }

    /**
     * Change delivery status
     * This method changes the status of the delivery of the given trip id
     * @param tripId the id of the trip
     * @param seqNum the sequence number of the delivery
     * @param deliveryStatus the status of the delivery
     */
    fun changeDeliveryStatus(tripId: Int, seqNum: Int, deliveryStatus: DeliveryStatusEnum) {
        viewModelScope.launch {
            tripListRepository.updateDeliveryStatus(tripId, seqNum, deliveryStatus)
        }
    }
}
