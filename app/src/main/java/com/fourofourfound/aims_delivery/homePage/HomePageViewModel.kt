package com.fourofourfound.aims_delivery.homePage

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.network.user.MakeNetworkCall
import com.fourofourfound.aims_delivery.repository.TripListRepository
import kotlinx.coroutines.launch

class HomePageViewModel(application: Application) : AndroidViewModel(application) {
    private val myApplication = application
    val database = getDatabase(application)
    val tripListRepository = TripListRepository(database)


    init {
        fetchTripFromNetwork()
    }

    fun fetchTripFromNetwork() {
        viewModelScope.launch {
            tripListRepository.refreshTrips()
        }
    }

    val tripList = tripListRepository.trips

    fun sendSavedLocation() {
        viewModelScope.launch {
            val location = database.tripListDao.getSavedLocation().apply {
                try {
                    MakeNetworkCall.retrofitService.sendLocation(this)
                    Log.i("Sending", "Try")
                } catch (e: Exception) {
                    Log.i("Sending", "Exception")
                }
            }

        }
    }


}
