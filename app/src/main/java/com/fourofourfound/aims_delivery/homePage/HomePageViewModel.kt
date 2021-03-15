package com.fourofourfound.aims_delivery.homePage

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.database.entities.DatabaseTrips
import com.fourofourfound.aims_delivery.database.entities.Fuel
import com.fourofourfound.aims_delivery.database.entities.Quantity
import com.fourofourfound.aims_delivery.database.entities.Source
import com.fourofourfound.aims_delivery.database.entities.location.LocationWithAddress
import com.fourofourfound.aims_delivery.database.entities.location.TripLocation
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.repository.TripListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Home page view model
 *This view model is used by the HomePageFragment to store the information about all trips
 * @constructor
 *
 * @param application the applicationContext which created this viewModel
 */
class HomePageViewModel(application: Application) : AndroidViewModel(application) {
    val database = getDatabase(application)
    private val tripListRepository = TripListRepository(database)

    /**
     * Trip list
     * List of trip that is a to be displayed
     */
    val tripList = tripListRepository.trips

    init {
        fetchTripFromNetwork()
    }

    /**
     * Fetch trip from network
     * It makes a network call to fetch updated trip list from the user
     */
    fun fetchTripFromNetwork() {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.tripListDao.insertSource(Source("s1", "Monroe", "t1"))
                database.tripListDao.insertSource(Source("s2", "Monroe1", "t1"))
                database.tripListDao.insertFuel(Fuel("Leaded", Quantity(10000, "GAL"), "s1"))
                database.tripListDao.insertLocation(
                    LocationWithAddress(
                        "McGFuire",
                        "Monroe",
                        "LA",
                        71203,
                        TripLocation(1.1, 1.2),
                        "s1"
                    )
                )

                database.tripListDao.insertTrip(DatabaseTrips("t1"))
                tripListRepository.refreshTrips()
                Log.i("AAAAAA", database.tripListDao.getAllTrip().toString())
            }
        }
    }


}
