package com.fourofourfound.aims_delivery.deliveryCompletionForm


import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.database.entities.DatabaseForm
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.repository.TripListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeliveryCompletionViewModel(
    val application: Application,
    val currentSourceOrSite: SourceOrSite
) : ViewModel() {
    val database = getDatabase(application)
    private val tripListRepository = TripListRepository(database)

    val billOfLadingNumber = 4444
    val productDesc = MutableLiveData(currentSourceOrSite.productDesc)
    val grossQty = MutableLiveData(currentSourceOrSite.requestedQty.toString())
    val netQty = MutableLiveData(currentSourceOrSite.requestedQty.toString())
    val comments = MutableLiveData(currentSourceOrSite.fill)
    val trailerBeginReading =
        MutableLiveData(currentSourceOrSite.requestedQty?.minus(100).toString())
    val trailerEndReadingCalc =
        Integer.parseInt(trailerBeginReading.value) - currentSourceOrSite.requestedQty!!
    val trailerEndReading = MutableLiveData(trailerEndReadingCalc.toString())

    val doneSubmitting = MutableLiveData(false)

    fun submitForm() {
        var formToSubmit = DatabaseForm(
            billOfLadingNumber,
            productDesc.value.toString(),
            "123",
            "123",
            "123",
            "123",
            Integer.parseInt(grossQty.value),
            Integer.parseInt(netQty.value),
            Integer.parseInt(trailerBeginReading.value),
            Integer.parseInt(trailerEndReading.value),
            comments.value!!
        )

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripListRepository.sendFormData(formToSubmit)
            }

            doneSubmitting.value = true

        }
    }

    fun doneNavigating() {
        doneSubmitting.value = false
    }

    fun markDeliveryCompleted(tripId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripListRepository.markDeliveryCompleted(tripId, currentSourceOrSite.seqNum)
            }

        }


    }
}