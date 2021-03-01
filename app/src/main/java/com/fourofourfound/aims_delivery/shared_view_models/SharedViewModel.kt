package com.fourofourfound.aims_delivery.shared_view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.utils.CheckInternetConnection

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    var userLoggedIn = MutableLiveData(false)
    var isLocationBroadcastReceiverInitialized: Boolean = false
    private val _selectedTrip = MutableLiveData<Trip>()

    val selectedTrip: LiveData<Trip>
        get() = _selectedTrip

    fun setSelectedTrip(trip: Trip?) {
        _selectedTrip.value = trip
    }

    val internetConnection = CheckInternetConnection(application)

}