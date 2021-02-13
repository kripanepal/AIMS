package com.fourofourfound.aims_delivery.delivery.completed

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.fourofourfound.aims_delivery.domain.Trip
import kotlinx.coroutines.launch

class CompletedDeliveryViewModel(trip: Trip) :ViewModel() {

    private val _completedTrip : Trip = trip
    val completedTrip: Trip
        get() = _completedTrip


}