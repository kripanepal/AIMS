package com.fourofourfound.aims_delivery.delivery.onGoing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.repository.TripListRepository
import kotlinx.coroutines.launch

/**
 * Ongoing delivery view model
 *This view model is used by the OngoingDeliveryFragment to store the information about ongoing
 * delivery
 * @constructor
 *
 * @param application the ApplicationContext used to create the viewModel
 */
class OngoingDeliveryViewModel(application: Application) :AndroidViewModel(application) {


    private val database = getDatabase(application)
    private val tripListRepository = TripListRepository(database)

    //information about the current trip being delivered
    private val _currentTrip = MutableLiveData<Trip>()
    val currentTrip: LiveData<Trip>
        get() = _currentTrip

    //used to inform the fragment that the delivery is completed
    private val _tripCompleted = MutableLiveData<Boolean>()
    val tripCompleted: LiveData<Boolean>
        get() = _tripCompleted


    fun setCurrentTrip(trip: Trip) {
        _currentTrip.value = trip
    }

    /**
     * Mark trip as completed
     *This function is called whenever the user marks any trip as completed
     * Local database is updated to reflect this change
     */
    fun markTripAsCompleted() {
        viewModelScope.launch {
            //TODO change status of trips
//            tripListRepository.markTripCompleted(currentTrip.value!!._id, true)
//            _tripCompleted.value = true

        }
    }


    //callback to ensure that the navigation is not triggered twice
    fun doneNavigatingToHomePage() {
        _tripCompleted.value = false
    }


}