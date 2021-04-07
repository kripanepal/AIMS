package com.fourofourfound.aims_delivery.delivery.onGoing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.repository.TripListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    fun updateFuelInfo(trailerId: Int, fuelQuantity: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripListRepository.updateTrailerFuel(trailerId, fuelQuantity)
            }

        }
    }


}