package com.fourofourfound.aims_delivery.homePage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.database.entities.StatusTable
import com.fourofourfound.aims_delivery.network.Driver
import com.fourofourfound.aims_delivery.repository.TripListRepository
import com.fourofourfound.aims_delivery.utils.getDatabaseForDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Home page view model
 *This view model is used by the HomePageFragment to store the information about all trips
 * @constructor
 *
 * @param application the applicationContext which created this viewModel
 */
class HomePageViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var driver: Driver
    val database = getDatabaseForDriver(application)

    private val tripListRepository = TripListRepository(database)
    var statusTable = listOf<StatusTable>()
    var statusTableAvailable = MutableLiveData(false)


    /**
     * Trip list
     * List of trip that is a to be displayed
     */
    val tripList = tripListRepository.trips

    var loaded = 0


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
            loaded ++
        }
    }

    fun getUpdatingTripsStatus() = tripListRepository.updatingTrips


    fun getStatusTableFromNetwork() {
        updating.value = true
        viewModelScope.launch {
            tripListRepository.getStatusTable()
            if(!statusTableAvailable.value!!)
            getStatusTableFromDatabase()
            updating.value = false
            statusTableAvailable.value = true
        }
    }
    private suspend fun getStatusTableFromDatabase() {
        withContext(Dispatchers.IO){
            statusTable = database.statusDao.getStatusTable()
        }
    }
}
