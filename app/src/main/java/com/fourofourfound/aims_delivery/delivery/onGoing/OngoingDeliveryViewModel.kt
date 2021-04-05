package com.fourofourfound.aims_delivery.delivery.onGoing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fourofourfound.aims_delivery.domain.Trip

/**
 * Ongoing delivery view model
 *This view model is used by the OngoingDeliveryFragment to store the information about ongoing
 * delivery
 * @constructor
 *
 * @param application the ApplicationContext used to create the viewModel
 */
class OngoingDeliveryViewModel(application: Application) :AndroidViewModel(application) {

    //information about the current trip being delivered
    private val _currentTrip = MutableLiveData<Trip>()
    val currentTrip: LiveData<Trip>
        get() = _currentTrip



    fun setCurrentTrip(trip: Trip) {
        _currentTrip.value = trip
    }






}