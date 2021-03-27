package com.fourofourfound.aims_delivery.homePage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.repository.TripListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Home page view model
 *This view model is used by the HomePageFragment to store the information about all trips
 * @constructor
 *
 * @param application the applicationContext which created this viewModel
 */
class HomePageViewModel(application: Application) : AndroidViewModel(application) {

    val database = getDatabase(application)
    private val tripListRepository = TripListRepository(database)

    /**
     * Trip list
     * List of trip that is a to be displayed
     */
    val tripList = tripListRepository.trips
    val updating = MutableLiveData(false)
    init {
        fetchTripFromNetwork()
    }

    /**
     * Fetch trip from network
     * It makes a network call to fetch updated trip list from the user
     */
    fun fetchTripFromNetwork() {
        updating.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripListRepository.refreshTrips()
            }
            updating.value = false
        }


    }

    fun getFuelTypes(tripId: String): List<String>? {
        var toReturn: List<String>?
        runBlocking {
            withContext(Dispatchers.IO) {
                toReturn = database.tripListDao.getAllProductsForTrip(tripId)
            }
        }
        return toReturn
    }

    fun getSourceName(fuelType: String, tripId: String): String {
        var toReturn: String
        runBlocking {
            withContext(Dispatchers.IO) {
                toReturn = database.tripListDao.getFuelSource(fuelType, tripId)
            }
        }
        return toReturn
    }

    fun getSiteCount(fuelType: String, tripId: String): Int {
        var toReturn: Int
        runBlocking {
            withContext(Dispatchers.IO) {
                toReturn = database.tripListDao.getSiteCount(fuelType, tripId)
            }
        }
        return toReturn
    }


}
