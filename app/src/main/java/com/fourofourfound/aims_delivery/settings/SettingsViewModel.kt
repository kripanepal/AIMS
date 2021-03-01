package com.fourofourfound.aims_delivery.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.CustomSharedPreferences
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.repository.TripListRepository
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) :AndroidViewModel(application) {
    private val myApplication = application
    private val database = getDatabase(application)
    private val tripListRepository = TripListRepository(database)


    fun logoutUser() {
        CustomSharedPreferences(myApplication).apply {
            deleteEncryptedPreference("username")
            deleteEncryptedPreference("password")
        }
        viewModelScope.launch {
            tripListRepository.deleteAllTrips()
        }
    }

}