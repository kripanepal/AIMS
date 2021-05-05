package com.fourofourfound.aims_delivery.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.repository.TripListRepository
import com.fourofourfound.aims_delivery.utils.CustomSharedPreferences
import com.fourofourfound.aims_delivery.utils.getDatabaseForDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Settings view model
 * This view model is used by the SettingsFragment to store the settings information
 * @constructor
 * @param application the applicationContext which created this viewModel
 */
class SettingsViewModel(application: Application) :AndroidViewModel(application) {
    /**
     * My application
     * Base class for maintaining global application state
     */
    private val myApplication = application

    /**
     * Database
     * The database for the currently logged in driver
     */
    var database = getDatabaseForDriver(application)

    /**
     * Trip list repository
     * The repository that holds the information about the trip and the destination
     */
    private var tripListRepository = TripListRepository(database)

    /**
     * Total trips completed
     * This live data hold the information about the total number of completed trips
     */
    var totalTripsCompleted = MutableLiveData(0)

    /**
     * Total deliveries completed
     * This live data hold the information about the total number of deliveries completed
     */
    var totalDeliveriesCompleted = MutableLiveData(0)

    /**
     * Loading
     * The live data that tells the application whether the loading animation needs to
     * be shown or not.
     */
    var loading = MutableLiveData(false)


    /**
     * Logout User
     * This method clears all the information of the user in the shared preferences
     */
    fun logoutUser() {

                CustomSharedPreferences(myApplication).apply {
                    deleteEncryptedPreference("username")
                    deleteEncryptedPreference("password")
                    deleteEncryptedPreference("driverCode")
                    deleteEncryptedPreference("id")
                    deleteEncryptedPreference("name")
                    deleteEncryptedPreference("companyId")
                    deleteEncryptedPreference("driverDescription")
                    deleteEncryptedPreference("compasDriverId")
                    deleteEncryptedPreference("truckId")
                    deleteEncryptedPreference("truckDescription")
                    deleteEncryptedPreference("active")
                }

    }

    /**
     * Get delivery data
     * This method gets the information about the completed trips and deliveries
     */
    fun getDeliveryData() {
        viewModelScope.launch {
            loading.value = true
            totalTripsCompleted.value = tripListRepository.getTotalTripsCompleted()
            totalDeliveriesCompleted.value = tripListRepository.getTotalDeliveriesMade()
            loading.value = false
        }
    }

    /**
     * Get database
     * This method gets the database for the currently logged in driver
     */
    fun getDatabase() {
        database = getDatabaseForDriver(myApplication)
        tripListRepository = TripListRepository(database)
    }


}