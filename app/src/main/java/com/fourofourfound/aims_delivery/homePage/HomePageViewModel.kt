package com.fourofourfound.aims_delivery.homePage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fourofourfound.aims_delivery.CustomSharedPreferences
import com.fourofourfound.aims_delivery.data.trip.TripInfo

class HomePageViewModel(application: Application) :AndroidViewModel(application) {
    private val myApplication = application

    private val _userLoggedIn = MutableLiveData<Boolean>()
    val userLoggedIn: LiveData<Boolean>
        get() = _userLoggedIn

    private val _tripList = MutableLiveData<List<TripInfo>>()
    val tripList: LiveData<List<TripInfo>>
        get() = _tripList


    fun logoutUser() {
        CustomSharedPreferences(myApplication).apply {
            deleteEncryptedPreference("username")
            deleteEncryptedPreference("password")
        }
        _userLoggedIn.value = false
    }



}