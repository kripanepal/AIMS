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


    val billOfLadingNumber = MutableLiveData<Int>(null)
    val productDesc = MutableLiveData(currentSourceOrSite.productInfo.productDesc)
    val grossQty: MutableLiveData<Int> =
        MutableLiveData(currentSourceOrSite.productInfo.requestedQty)
    val netQty: MutableLiveData<Int> = MutableLiveData(currentSourceOrSite.productInfo.requestedQty)
    val comments = MutableLiveData(currentSourceOrSite.productInfo.fill)
    val trailerBeginReading: MutableLiveData<Int> =
        MutableLiveData(currentSourceOrSite.trailerInfo.fuelQuantity)
    val trailerEndReadingCalc =
        trailerBeginReading.value!! - currentSourceOrSite.productInfo.requestedQty!!
    val trailerEndReading: MutableLiveData<Int> = MutableLiveData(trailerEndReadingCalc)
    var startTime: Calendar = Calendar.getInstance()
    var endTime: Calendar = Calendar.getInstance()
    var startDate: Calendar = Calendar.getInstance()
    var endDate: Calendar = Calendar.getInstance()


    fun submitForm() {
        var formToSubmit = DatabaseForm(
            billOfLadingNumber.value!!,
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
            grossQty.value!!,
            netQty.value!!,
            trailerBeginReading.value!!,
            trailerEndReading.value!!,
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