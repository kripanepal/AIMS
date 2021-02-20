package com.fourofourfound.aims_delivery.homePage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.repository.TripListRepository
import kotlinx.coroutines.launch

class HomePageViewModel(application: Application) : AndroidViewModel(application) {
    private val myApplication = application

    private val database = getDatabase(application)
    private val tripListRepository = TripListRepository(database)

    val locationEnabled = MutableLiveData<Boolean>()


    val locationPermissionGranted = MutableLiveData<Boolean>(true)


    init {
        fetchTripFromNetwork()
    }

    fun fetchTripFromNetwork() {
        viewModelScope.launch {
            tripListRepository.refreshTrips()
        }
    }

    val tripList = tripListRepository.trips




}