package com.fourofourfound.aims_delivery.delivery.completed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.database.relations.CompletedFormWithInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Completed delivery view model
 *This view model is used by the CompletedDeliveryFragment to store the information about completed
 * deliveries
 * @constructor
 * Creates a new view model with a trip whose information is to be displayed
 *
 * @param trip trip whose information is to be displayed
 */
class CompletedDeliveryViewModel(application: Application) : AndroidViewModel(application) {



    var loading = MutableLiveData(true)

    val database = getDatabase(application)

    var tripDetails= listOf<CompletedFormWithInfo>()

    fun getTripDetails(tripId: Int, seqNo: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if(seqNo == -1) tripDetails = database.completedDeliveriesDao.getDetails(tripId)
                else tripDetails = database.completedDeliveriesDao.getDetailsForDestination(tripId, seqNo)

            }
            loading.value = false

        }
    }

}