package com.fourofourfound.aims_delivery.shared_view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fourofourfound.aims_delivery.domain.Trip

class SharedViewModel:ViewModel() {
    private val _selectedTrip = MutableLiveData<Trip>()
    val selectedTrip: LiveData<Trip>
        get() = _selectedTrip

    fun setSelectedTrip(trip:Trip)
    {
        _selectedTrip.value = trip
    }
}