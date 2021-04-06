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
import java.util.*

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
        MutableLiveData(currentSourceOrSite.trailerInfo.fuelQuantity.toString())
    private val trailerEndReadingCalc =
        Integer.parseInt(trailerBeginReading.value) - currentSourceOrSite.productInfo.requestedQty!!
    val trailerEndReading = MutableLiveData(trailerEndReadingCalc.toString())
    var startTime = Calendar.getInstance()
    var endTime = Calendar.getInstance()
    var startDate = Calendar.getInstance()
    var endDate = Calendar.getInstance()


    fun submitForm() {
        var formToSubmit = DatabaseForm(
            billOfLadingNumber,
            productDesc.value.toString(),
            "${startDate.get(Calendar.YEAR)} ${startDate.get(Calendar.MONTH).plus(1)} ${
                startDate.get(
                    Calendar.DAY_OF_MONTH
                )
            }",
            startTime.get(Calendar.HOUR_OF_DAY).toString() + " " + startTime.get(Calendar.MINUTE),
            "${endDate.get(Calendar.YEAR)} ${endDate.get(Calendar.MONTH).plus(1)} ${
                endDate.get(
                    Calendar.DAY_OF_MONTH
                )
            }",
            endTime.get(Calendar.HOUR_OF_DAY).toString() + " " + endTime.get(Calendar.MINUTE),
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