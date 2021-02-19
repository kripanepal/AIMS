package com.fourofourfound.aims_delivery.settings

import android.app.Application
import androidx.lifecycle.*
import com.fourofourfound.aims_delivery.CustomSharedPreferences
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.repository.TripListRepository
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) :AndroidViewModel(application) {
    private val myApplication = application
    private val database = getDatabase(application)
    private val tripListRepository = TripListRepository(database)

    private val _userLoggedIn = MutableLiveData<Boolean>()
    val userLoggedIn: LiveData<Boolean>
        get() = _userLoggedIn

    fun logoutUser() {
        CustomSharedPreferences(myApplication).apply {
            deleteEncryptedPreference("username")
            deleteEncryptedPreference("password")
        }
        viewModelScope.launch {
            tripListRepository.deleteAllTrips()
        }
        _userLoggedIn.value = false
    }

}