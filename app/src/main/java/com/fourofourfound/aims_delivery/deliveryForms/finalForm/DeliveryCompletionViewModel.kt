package com.fourofourfound.aims_delivery.deliveryForms.finalForm


import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.database.entities.DatabaseCompletionForm
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.repository.TripListRepository
import com.fourofourfound.aims_delivery.utils.DeliveryStatusEnum
import com.fourofourfound.aims_delivery.utils.getDatabaseForDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class DeliveryCompletionViewModel(
    val application: Application,
    val currentSourceOrSite: SourceOrSite
) : ViewModel() {
    val database = getDatabaseForDriver(application)
    private val tripListRepository = TripListRepository(database)
    var tripId = 0
    val destination = currentSourceOrSite
    val billOfLadingNumber = MutableLiveData<String>(null)
    var productDesc = MutableLiveData(currentSourceOrSite.productInfo.productDesc)
    val grossQty: MutableLiveData<Int> =
        MutableLiveData(currentSourceOrSite.productInfo.requestedQty)
    val netQty: MutableLiveData<Int> = MutableLiveData(currentSourceOrSite.productInfo.requestedQty)
    val comments = MutableLiveData(currentSourceOrSite.productInfo.fill)
    val trailerBeginReading: MutableLiveData<Double> =
        MutableLiveData(currentSourceOrSite.trailerInfo.fuelQuantity)
    private val trailerEndReadingCalc =
        if (currentSourceOrSite.wayPointTypeDescription == "Source") trailerBeginReading.value!! + currentSourceOrSite.productInfo.requestedQty!! else trailerBeginReading.value!! - currentSourceOrSite.productInfo.requestedQty!!
    val trailerEndReading: MutableLiveData<Double> = MutableLiveData(trailerEndReadingCalc)
    var startTime: Calendar = Calendar.getInstance()
    var endTime: Calendar = Calendar.getInstance()
    var productList = MutableLiveData<List<String>>()
    var stickReadingBefore = MutableLiveData<Double>(null)
    var stickReadingAfter = MutableLiveData<Double>(null)
    var meterReadingBefore = MutableLiveData<Double>(null)
    var meterReadingAfter = MutableLiveData<Double>(null)
    var imageBitmaps = MutableLiveData<MutableList<Bitmap>>()
    var imagePaths = MutableLiveData<MutableList<String>>()
    var formSubmitted = MutableLiveData(false)

    fun submitForm() {
        var formToSubmit = DatabaseCompletionForm(
            billOfLadingNumber.value,
            productDesc.value!!,
            startTime,
            endTime,

            grossQty.value!!,
            netQty.value!!,
            trailerBeginReading.value!!,
            trailerEndReading.value!!,
            comments.value!!,
            currentSourceOrSite.seqNum,
            tripId,
            stickReadingBefore.value,
            stickReadingAfter.value,
            meterReadingBefore.value,
            meterReadingAfter.value


        )

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripListRepository.sendFormData(formToSubmit)
                tripListRepository.addBillOfLadingImages(
                    tripId,
                    currentSourceOrSite.seqNum,
                    imagePaths.value
                )
            }
            formSubmitted.value = true
        }
    }

    init {
        getProducts()
    }

    fun updateDeliveryStatus(tripId: Int, deliveryStatus: DeliveryStatusEnum) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripListRepository.updateDeliveryStatus(tripId, currentSourceOrSite.seqNum, deliveryStatus)
            }
        }
    }

    private fun getProducts() {
        viewModelScope.launch {
            productList.value = database.productsDao.getProducts()
        }
    }


    operator fun <T> MutableLiveData<ArrayList<T>>.plusAssign(values: List<T>) {
        val value = this.value ?: arrayListOf()
        value.addAll(values)
        this.value = value
    }

    operator fun <T> MutableLiveData<ArrayList<T>>.minusAssign(values: List<T>) {
        val value = this.value ?: arrayListOf()
        value.removeAll(values)
        this.value = value
    }


}