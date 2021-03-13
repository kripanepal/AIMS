package com.fourofourfound.aims_delivery.homePage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.database.entities.fuel.Fuel
import com.fourofourfound.aims_delivery.database.entities.location.LocationWithAddress
import com.fourofourfound.aims_delivery.database.entities.source.Source
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.repository.TripListRepository
import kotlinx.coroutines.launch

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
            database.tripListDao.insertFuel(Fuel("Leaded", 10000, "GAL", "s1"))
            database.tripListDao.insertFuel(Fuel("ULeaded", 1000, "GAL", "s2"))
            database.tripListDao.insertFuel(Fuel("PLeaded", 100, "GAL", "s3"))
            database.tripListDao.insertFuel(Fuel("DLeaded", 10, "GAL", "s4"))

            database.tripListDao.insertSource(Source("s1", "Source 1"))
            database.tripListDao.insertSource(Source("s2", "Source 2"))
            database.tripListDao.insertSource(Source("s3", "Source 3"))
            database.tripListDao.insertSource(Source("s4", "Source 4"))

            database.tripListDao.insertLocation(
                LocationWithAddress(
                    "McGuire",
                    "Monroe",
                    "LA",
                    71203,
                    1.11,
                    1.2,
                    "s1"
                )
            )
            database.tripListDao.insertLocation(
                LocationWithAddress(
                    "McGuiree",
                    "Monroe1",
                    "LA",
                    712033,
                    1.111,
                    1.2,
                    "s2"
                )
            )
            database.tripListDao.insertLocation(
                LocationWithAddress(
                    "McGuireee",
                    "Monroe2",
                    "LA",
                    712034,
                    1.12,
                    1.2,
                    "s3"
                )
            )
            database.tripListDao.insertLocation(
                LocationWithAddress(
                    "McGuireeee",
                    "Monroe3",
                    "LA",
                    71205,
                    1.13,
                    1.2,
                    "s4"
                )
            )
            database.tripListDao.insertLocation(
                LocationWithAddress(
                    "McGuireeee",
                    "Monroe3",
                    "LA",
                    71205,
                    1.14,
                    1.2,
                    "s5"
                )
            )

            var result = database.tripListDao.getSource()

            tripListRepository.refreshTrips()
        }
    }


}
