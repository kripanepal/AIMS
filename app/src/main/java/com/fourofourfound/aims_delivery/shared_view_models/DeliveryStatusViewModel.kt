package com.fourofourfound.aims_delivery.shared_view_models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.database.entities.DatabaseStatusPut
import com.fourofourfound.aims_delivery.domain.DestinationLocation
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.network.MakeNetworkCall
import com.fourofourfound.aims_delivery.repository.LocationRepository
import com.fourofourfound.aims_delivery.utils.getDatabaseForDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

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
    private val myApplication = application
    var database = getDatabaseForDriver(application)

    fun getUpdatedDatabase()
    {
        database = getDatabaseForDriver(myApplication)
    }

    fun sendStatusUpdate(
        toPut: DatabaseStatusPut,
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO)
            {
                try {
                    toPut.apply {
                        MakeNetworkCall.retrofitService.sendStatusUpdate(
                            driverCode,
                            tripId,
                            statusCode,
                            statusMessage,
                            statusDate
                        )
                        Log.i("AAAAAAAAAAAA",toPut.toString())
                    }

                } catch (ex: HttpException) {
                    database.statusPutDao.insertPutData(toPut)
                } catch (ex: Exception) {
                    database.statusPutDao.insertPutData(toPut)
                }

            }

        }
    }




   var previousDestination: SourceOrSite? = null
    val locationRepository = LocationRepository(application)

    var destinationApproachingShown = false
    var destinationLeavingShown = false

}