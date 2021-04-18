package com.fourofourfound.aims_delivery.delivery.onGoing

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.repository.TripListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Ongoing delivery view model
 *This view model is used by the OngoingDeliveryFragment to store the information about ongoing
 * delivery
 * @constructor
 *
 * @param application the ApplicationContext used to create the viewModel
 */
class OngoingDeliveryViewModel(application: Application) :AndroidViewModel(application) {
    val database = getDatabase(application)
    private val tripListRepository = TripListRepository(database)
    var destination :SourceOrSite? = null
    lateinit var startDateAndTime: Calendar
    lateinit var endDateAndTime: Calendar
    var stickReadingBegin = MutableLiveData(0.0)
    var meterReadingBegin = MutableLiveData(0.0)
    var trailerReadingBegin = MutableLiveData(0.0)
    var stickReadingEnd = MutableLiveData(0.0)
    var meterReadingEnd = MutableLiveData(0.0)
    var trailerReadingEnd = MutableLiveData(0.0)



    var destinationApproaching = false
    var fillingStarted = MutableLiveData(false)
    var fillingEnded = MutableLiveData(false)


    fun updateFuelInfo(trailerId: Int, fuelQuantity: Double) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripListRepository.updateTrailerFuel(trailerId, fuelQuantity)
            }

        }
    }

}