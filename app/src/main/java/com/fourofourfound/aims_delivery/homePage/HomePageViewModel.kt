package com.fourofourfound.aims_delivery.homePage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.network.Driver
import com.fourofourfound.aims_delivery.repository.TripListRepository
import com.fourofourfound.aims_delivery.utils.getDatabaseForDriver
import kotlinx.coroutines.launch

/**
 * Home page view model
 * This view model is used by the HomePageFragment to store the information about all trips
 * @constructor
 * @param application the applicationContext which created this viewModel
 */
class HomePageViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Driver
     * The currently logged in driver
     */
    lateinit var driver: Driver

    /**
     * Database
     * The database for the currently logged in driver
     */
    val database = getDatabaseForDriver(application)

    /**
     * Trip list repository
     * The repository that holds the information about the trip and the destination.
     */
    private val tripListRepository = TripListRepository(database)

    /**
     * Trip list
     * List of trip that is a to be displayed
     */
    val tripList = tripListRepository.trips

    /**
     * Loaded
     * Counter to check if the fragment is already loaded or not.
     */
    var loaded = 0

    /**
     * Updating
     * The live data to check of the trip is updating.
     */
    val updating = MutableLiveData(false)

    /**
     * Fetch trip from network
     * It makes a network call to fetch updated trip list from the user
     */
    fun fetchTripFromNetwork(code: String) {
        updating.value = true
        viewModelScope.launch {
            tripListRepository.refreshTrips(code)

            updating.value = false
            loaded++
        }
    }

    /**
     * Get updating trips status
     * This method return the updating trip status.
     */
    fun getUpdatingTripsStatus() = tripListRepository.updatingTrips
}
