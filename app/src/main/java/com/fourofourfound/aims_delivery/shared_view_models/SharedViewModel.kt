package com.fourofourfound.aims_delivery.shared_view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fourofourfound.aims_delivery.broadcastReceiver.NetworkChangedBroadCastReceiver
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.network.Driver
import com.fourofourfound.aims_delivery.utils.CheckInternetConnection

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


    var workerStarted: Boolean = false

    /**
     * User logged in
     * The live data that keeps track of the user logged in status
     */
    var userLoggedIn = MutableLiveData(false)

    /**
     * Is location broadcast receiver initialized
     * It keeps  track of the broadcast receiver state
     */
    var broadCastReceiver = NetworkChangedBroadCastReceiver()

    var broadCastReceiverInitialized = false

    /**
     * Active route
     * The current route in the application if the application is in navigation
     */
    var activeRoute: com.here.android.mpa.routing.Route? = null

    /**
     * Selected trip
     * The live data that keeps track of the current trip
     */
    var selectedTrip = MutableLiveData<Trip?>()

    /**
     * Selected source or site
     * The live data that keep track of current destination
     */
    var selectedSourceOrSite = MutableLiveData<SourceOrSite>()

    /**
     * Internet connection
     * It keeps track of the internet connection state
     */
    val internetConnection = CheckInternetConnection(application)

    /**
     * Loading
     * The live data that tells the application whether the loading animation needs to
     * be shown or not.
     */
    val loading = MutableLiveData(false)

    /**
     * Driver
     * The current driver who is logged into the app.
     */
    var driver: Driver? = null


}