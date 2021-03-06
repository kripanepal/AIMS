package com.fourofourfound.aims_delivery.delivery.completed

import androidx.lifecycle.ViewModel
import com.fourofourfound.aims_delivery.domain.Trip

/**
 * Completed delivery view model
 *This view model is used by the CompletedDeliveryFragment to store the information about completed
 * deliveries
 * @constructor
 * Creates a new view model with a trip whose information is to be displayed
 *
 * @param trip trip whose information is to be displayed
 */
class CompletedDeliveryViewModel(trip: Trip) :ViewModel() {

    //abstracting the trip details so that it cannot be modified
    private val _completedTrip : Trip = trip
    val completedTrip: Trip
        get() = _completedTrip

}