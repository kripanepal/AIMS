package com.fourofourfound.aims_delivery.delivery.onGoing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.repository.TripListRepository
import com.fourofourfound.aims_delivery.utils.getDatabaseForDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Ongoing delivery view model
 * This view model is used by the OngoingDeliveryFragment to store the information about ongoing
 * delivery.
 * @constructor
 *
 * @param application the ApplicationContext used to create the viewModel
 */
class OngoingDeliveryViewModel(application: Application, destination: SourceOrSite) :
    AndroidViewModel(application) {

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
     * Trailer reading
     * The live data that holds the information about the trailer.
     */
    var trailerReading = database.trailerDao.getTrailerReading(destination.trailerInfo.trailerId)

    /**
     * Start date and time
     * The start date and time of the delivery.
     */
    lateinit var startDateAndTime: Calendar

    /**
     * End date and time
     * The end date and time of the delivery.
     */
    lateinit var endDateAndTime: Calendar

    /**
     * Stick reading begin
     * The live data for the beginning stick reading.
     */
    var stickReadingBegin = MutableLiveData(0.0)

    /**
     * Meter reading begin
     * The live date for the begin meter reading.
     */
    var meterReadingBegin = MutableLiveData(0.0)

    /**
     * Trailer reading begin
     * The live data for the begin trailer reading.
     */
    var trailerReadingBegin = MutableLiveData(0.0)

    /**
     * Stick reading end
     * The live data for the end stick reading.
     */
    var stickReadingEnd = MutableLiveData(0.0)

    /**
     * Meter reading end
     * The live data for end meter reading.
     */
    var meterReadingEnd = MutableLiveData(0.0)

    /**
     * Trailer reading end
     * The live data for end trailer reading.
     */
    var trailerReadingEnd = MutableLiveData(0.0)

    /**
     * Filling started
     * The flag to check if the filling is started or not.
     */
    var fillingStarted = MutableLiveData(false)

    /**
     * Filling ended
     * The flag to check if the filling is ended or not.
     */
    var fillingEnded = MutableLiveData(false)

    /**
     * Update Fuel Info
     * This method updates the fuel information.
     * @param trailerId Int the id of the trailer.
     * @param fuelQuantity Double the fuel quantity
     */
    fun updateFuelInfo(trailerId: Int, fuelQuantity: Double) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripListRepository.updateTrailerFuel(trailerId, fuelQuantity)
            }
        }
    }

    /**
     * Get database
     * This method gets the database for the driver.
     */
    fun getDatabase() {
        database = getDatabaseForDriver(myApplication)
        tripListRepository = TripListRepository(database)
    }
}