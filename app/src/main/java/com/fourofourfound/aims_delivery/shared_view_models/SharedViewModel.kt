package com.fourofourfound.aims_delivery.shared_view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.database.entities.Driver
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.domain.Trip
import com.fourofourfound.aims_delivery.utils.CheckInternetConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    var activeRoute: com.here.android.mpa.routing.Route? = null
    var selectedTrip = MutableLiveData<Trip?>()
    var selectedSourceOrSite = MutableLiveData<SourceOrSite>()
    val internetConnection = CheckInternetConnection(application)
    val loading = MutableLiveData(false)
    lateinit var driver: Driver

    fun getDriver(application: Application) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
               getDatabase(application).driverDao.getDriver()?.apply {
                    driver = this
                }
            }
        }
    }
}