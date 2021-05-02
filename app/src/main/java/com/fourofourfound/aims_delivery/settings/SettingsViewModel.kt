package com.fourofourfound.aims_delivery.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.CustomSharedPreferences
import com.fourofourfound.aims_delivery.repository.TripListRepository
import com.fourofourfound.aims_delivery.utils.getDatabaseForDriver
import kotlinx.coroutines.launch

/**
 * Settings view model
 * This view model is used by the SettingsFragment to store the settings information
 *
 * @constructor
 * @param application the applicationContext which created this viewModel
 */
class SettingsViewModel(application: Application) :AndroidViewModel(application) {
    private val myApplication = application
    var database = getDatabaseForDriver(application)

    private var tripListRepository = TripListRepository(database)
    var totalTripsCompleted = MutableLiveData(0)
    var totalDeliveriesCompleted = MutableLiveData(0)
    var loading = MutableLiveData(false)

    /**
     * Logout User
     * This method logs the user out of the application
     * upon clicking the logout button.
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
        database.close()
    }

    fun getDeliveryData() {
        viewModelScope.launch {
            loading.value = true
            totalTripsCompleted.value = tripListRepository.getTotalTripsCompleted()
            totalDeliveriesCompleted.value = tripListRepository.getTotalDeliveriesMade()
            loading.value = false
        }
    }

    fun getDatabase() {
        database = getDatabaseForDriver(myApplication)
        tripListRepository = TripListRepository(database)
    }


}