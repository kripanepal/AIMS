package com.fourofourfound.aims_delivery.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.CustomSharedPreferences
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.repository.TripListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Settings view model
 * This view model is used by the SettingsFragment to store the settings information
 *
 * @constructor
 * @param application the applicationContext which created this viewModel
 */
class SettingsViewModel(application: Application) :AndroidViewModel(application) {
    private val myApplication = application
    private val database = getDatabase(application)
    private val tripListRepository = TripListRepository(database)


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
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.logoutDao.deleteAll()
            }


        }
    }

}