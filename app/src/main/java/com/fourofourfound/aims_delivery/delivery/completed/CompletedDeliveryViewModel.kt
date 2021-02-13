package com.fourofourfound.aims_delivery.delivery.completed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.CustomSharedPreferences
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.repository.TripListRepository
import kotlinx.coroutines.launch

class CompletedDeliveryViewModel(application: Application) :AndroidViewModel(application) {



    private val _tripCompleted = MutableLiveData<Boolean>()
    val tripCompleted: LiveData<Boolean>
        get() = _tripCompleted






    //callback to ensure that the navigation is not triggered twice
    fun doneNavigatingToHomePage()
    {
        _tripCompleted.value = false
    }




}