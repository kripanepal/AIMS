package com.fourofourfound.aims_delivery.shared_view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.utils.CheckInternetConnection
import com.here.android.mpa.routing.Route

/**
 * Shared View Model
 * This view model stores the information about current user, connection status and
 * currently selected trip. This View Model is used buy multiple fragments
 *
 * @constructor
 *
 * @param application the applicationContext which created this viewModel
 */
class SharedViewModel(application: Application) : AndroidViewModel(application) {
    var userLoggedIn = MutableLiveData(false)
    var isLocationBroadcastReceiverInitialized: Boolean = false
    var activeRoute: Route? = null
    var selectedTrip = MutableLiveData<Trip>()
    val internetConnection = CheckInternetConnection(application)

}