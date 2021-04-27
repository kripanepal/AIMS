package com.fourofourfound.aims_delivery.shared_view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.fourofourfound.aims_delivery.domain.DestinationLocation
import com.fourofourfound.aims_delivery.repository.LocationRepository

/**
 * Shared View Model
 * This view model stores the information about current user, connection status and
 * currently selected trip. This View Model is used buy multiple fragments
 *
 * @constructor
 *
 * @param application the applicationContext which created this viewModel
 */
class DeliveryStatusViewModel(application: Application) : AndroidViewModel(application) {


    var previousDestination: DestinationLocation? = null

    val locationRepository = LocationRepository(application)
    var destinationApproachingShown = false
    var destinationLeavingShown = false

}