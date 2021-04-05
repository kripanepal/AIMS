package com.fourofourfound.aims_delivery.deliveryCompletionForm


import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.database.entities.DatabaseForm
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.repository.TripListRepository
import com.fourofourfound.aims_delivery.utils.StatusEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeliveryCompletionViewModel(
    val application: Application,
    private val currentSourceOrSite: SourceOrSite
) : ViewModel() {
    val database = getDatabase(application)
    private val tripListRepository = TripListRepository(database)

    private val billOfLadingNumber = 4444
    val productDesc = MutableLiveData(currentSourceOrSite.productInfo.productDesc)
    val grossQty = MutableLiveData(currentSourceOrSite.productInfo.requestedQty.toString())
    val netQty = MutableLiveData(currentSourceOrSite.productInfo.requestedQty.toString())
    val comments = MutableLiveData(currentSourceOrSite.productInfo.fill)
    val trailerBeginReading =
        MutableLiveData(currentSourceOrSite.productInfo.requestedQty?.minus(100).toString())
    private val trailerEndReadingCalc =
        Integer.parseInt(trailerBeginReading.value) - currentSourceOrSite.productInfo.requestedQty!!
    val trailerEndReading = MutableLiveData(trailerEndReadingCalc.toString())


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

        }
    }


    fun updateDeliveryStatus(tripId: Int, status: StatusEnum) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripListRepository.updateDeliveryStatus(tripId, currentSourceOrSite.seqNum, status)
            }
        }
    }
}