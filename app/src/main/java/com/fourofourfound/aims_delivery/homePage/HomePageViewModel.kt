package com.fourofourfound.aims_delivery.homePage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.CustomSharedPreferences
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.repository.TripListRepository
import kotlinx.coroutines.launch

class HomePageViewModel(application: Application) :AndroidViewModel(application) {
    private val myApplication = application

    private val _userLoggedIn = MutableLiveData<Boolean>()
    val userLoggedIn: LiveData<Boolean>
        get() = _userLoggedIn


    private val _navigate = MutableLiveData<Boolean>()
    val navigate: LiveData<Boolean>
        get() = _navigate


    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading


    private val database = getDatabase(application)
    private val tripListRepository = TripListRepository(database)

    init {
        fetchTripFromNetwork()
    }

    fun fetchTripFromNetwork() {
        viewModelScope.launch {
            tripListRepository.refreshTrips()
        }
    }

    val tripList = tripListRepository.trips


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