package com.fourofourfound.aims_delivery.deliveryForms.finalForm


import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourofourfound.aims_delivery.database.entities.DatabaseCompletionForm
import com.fourofourfound.aims_delivery.database.entities.DatabaseFuel
import com.fourofourfound.aims_delivery.database.utilClasses.ProductData
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aims_delivery.repository.TripListRepository
import com.fourofourfound.aims_delivery.shared_view_models.DeliveryStatusViewModel
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
    var driver = ""
    var tripId = -1
    val destination = currentSourceOrSite
    val billOfLadingNumber = MutableLiveData<String>(null)
    var productDesc = MutableLiveData(currentSourceOrSite.productInfo.productDesc)
    val grossQty: MutableLiveData<Int> =
        MutableLiveData(currentSourceOrSite.productInfo.requestedQty)
    val netQty: MutableLiveData<Int> = MutableLiveData(currentSourceOrSite.productInfo.requestedQty)
    val comments = MutableLiveData("")
    val trailerBeginReading: MutableLiveData<Double> =
        MutableLiveData(currentSourceOrSite.trailerInfo.fuelQuantity)
    private val trailerEndReadingCalc =
        if (currentSourceOrSite.wayPointTypeDescription == "Source") trailerBeginReading.value!! + currentSourceOrSite.productInfo.requestedQty!! else trailerBeginReading.value!! - currentSourceOrSite.productInfo.requestedQty!!
    val trailerEndReading: MutableLiveData<Double> = MutableLiveData(trailerEndReadingCalc)
    var startTime: Calendar = Calendar.getInstance()
    var endTime: Calendar = Calendar.getInstance()
    var productList = MutableLiveData<List<String>>()
    var productListWithAllInfo = MutableLiveData<List<DatabaseFuel>>()
    var stickReadingBefore = MutableLiveData<Double>(null)
    var stickReadingAfter = MutableLiveData<Double>(null)
    var meterReadingBefore = MutableLiveData<Double>(null)
    var meterReadingAfter = MutableLiveData<Double>(null)
    var imagePaths = MutableLiveData<MutableList<String>>()
    var navigate = MutableLiveData(false)
    var loading = MutableLiveData(false)



    fun submitForm() {
        loading.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    var productId = checkIfProductsAreSame()

                    var formToSubmit = DatabaseCompletionForm(
                        driver,
                        (destination.siteId ?: destination.sourceId)!!,
                        billOfLadingNumber.value ?: "Not Provided",
                        destination.productInfo.productId,
                        productId,
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
                        meterReadingAfter.value,
                        destination.wayPointTypeDescription
                    )


                    tripListRepository.sendFormData(formToSubmit)
                    tripListRepository.addBillOfLadingImages(
                        tripId,
                        currentSourceOrSite.seqNum,
                        imagePaths.value
                    )

                    sendProductPickedUpMessage(productId)
                } catch (e: Exception) {

                }


            }

            loading.value = false
            navigate.value = true
        }


    }

    private  fun sendProductPickedUpMessage(productId: Int) {
            val dataToSend = ProductData(
                driver,
                tripId,
                (destination.siteId ?: destination.sourceId)!!,
                productId,
                billOfLadingNumber.value ?: "Not Provided",
                (startTime),
                (endTime),
                grossQty.value!!,
                netQty.value!!,
                trailerEndReading.value!!,
                destination.wayPointTypeDescription
            )
            DeliveryStatusViewModel.sendProductFulfilledMessage(dataToSend,database)

    }

    private suspend fun checkIfProductsAreSame(): Int {
        var productId = destination.productInfo.productId
        val productSelected = productDesc.value.toString().trim()

        //different product is selected
        if (productSelected != destination.productInfo.productDesc.toString().trim()) {
            var isProductNew = true
            for (product in productListWithAllInfo.value!!) {
                if (product.productDesc!!.trim() == productSelected) {
                    productId = product.productId
                    isProductNew = false
                    break
                }

            }

            if (isProductNew) {
                val minProductId = (productListWithAllInfo.value!!.map { it.productId }).maxOrNull()
                if (minProductId != null) {
                    viewModelScope.launch {
                        withContext(Dispatchers.IO)
                        {
                            database.productsDao.insertFuel(
                                DatabaseFuel(
                                    minProductId + 1,
                                    productSelected,
                                    productSelected
                                )
                            )
                        }
                    }

                }
            }
        }
        return productId
    }

    init {
        getProducts()
    }

    fun updateDeliveryStatus(tripId: Int, deliveryStatus: DeliveryStatusEnum) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                tripListRepository.updateDeliveryStatus(
                    tripId,
                    currentSourceOrSite.seqNum,
                    deliveryStatus
                )
            }
        }
    }

    private fun getProducts() {
        viewModelScope.launch {
            var productListWithInfo: List<DatabaseFuel>?
            withContext(Dispatchers.IO)

            {
                productListWithInfo = database.productsDao.getProducts()

            }
            productListWithAllInfo.value = productListWithInfo!!
            productList.value = productListWithAllInfo.value!!.map {
                it.productDesc.toString()
            }
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

    fun getUnsentProductPickedUpList() {
        viewModelScope.launch {
            withContext(Dispatchers.IO)
            {


            }

        }
    }


}