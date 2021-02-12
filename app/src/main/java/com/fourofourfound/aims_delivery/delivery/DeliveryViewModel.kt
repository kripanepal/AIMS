package com.fourofourfound.aims_delivery.delivery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.CustomSharedPreferences
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.repository.TripListRepository
import kotlinx.coroutines.launch

class DeliveryViewModel(application: Application) :AndroidViewModel(application) {
    private val myApplication = application
    private val database = getDatabase(application)
    private val tripListRepository = TripListRepository(database)

    private val _currentTrip = MutableLiveData<Trip>()
    val currentTrip: LiveData<Trip>
        get() = _currentTrip

    private val _tripCompleted = MutableLiveData<Boolean>()
    val tripCompleted: LiveData<Boolean>
        get() = _tripCompleted

    fun setCurrentTrip(trip:Trip)
    {
        _currentTrip.value = trip
    }



    fun markTripAsCompleted()
    {
        viewModelScope.launch {
            tripListRepository.markTripCompleted(currentTrip.value!!._id,true)
            _tripCompleted.value = true

        }
    }


    //callback to ensure that the navigation is not triggered twice
    fun doneNavigatingToHomePage()
    {
        _tripCompleted.value = false
    }




}