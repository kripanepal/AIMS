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

/**
 * Delivery completion view model
 * This class is responsible for holding the data for the delivery form.
 * @property application Application Base class for maintaining global application state.
 * @property currentSourceOrSite SourceOrSite current destination
 * @constructor Create empty Delivery completion view model
 */
class DeliveryCompletionViewModel(
    val application: Application,
    val currentSourceOrSite: SourceOrSite
) : ViewModel() {

    /**
     * Database
     * The database used by the view model.
     */
    val database = getDatabaseForDriver(application)

    /**
     * Trip list repository
     * The repository that holds the information about the trip and the destination
     */
    private val tripListRepository = TripListRepository(database)

    /**
     * Driver
     * The current driver code.
     */
    var driver = ""

    /**
     * Trip id
     * The current trip id.
     */
    var tripId = -1

    /**
     * Destination
     * The current destination.
     */
    val destination = currentSourceOrSite

    /**
     * Bill of lading number
     * The live data of the bill of lading number.
     */
    val billOfLadingNumber = MutableLiveData<String>(null)

    /**
     * Product desc
     * The live data of description of the product.
     */
    var productDesc = MutableLiveData(currentSourceOrSite.productInfo.productDesc)

    /**
     * Gross qty
     * The live data of gross quantity of the fuel.
     */
    val grossQty: MutableLiveData<Int> =
        MutableLiveData(currentSourceOrSite.productInfo.requestedQty)

    /**
     * Net qty
     * The live data of net quantity of the fuel.
     */
    val netQty: MutableLiveData<Int> = MutableLiveData(currentSourceOrSite.productInfo.requestedQty)

    /**
     * Comments
     * The live data of the comments added in the form.
     */
    val comments = MutableLiveData("")

    /**
     * Trailer begin reading
     * The live data of trailer begin reading.
     */
    val trailerBeginReading: MutableLiveData<Double> =
        MutableLiveData(currentSourceOrSite.trailerInfo.fuelQuantity)

    /**
     * Trailer end reading calc
     * The calculated vale for the trailer end reading.
     */
    private val trailerEndReadingCalc =
        if (currentSourceOrSite.wayPointTypeDescription == "Source") trailerBeginReading.value!! + currentSourceOrSite.productInfo.requestedQty!! else trailerBeginReading.value!! - currentSourceOrSite.productInfo.requestedQty!!

    /**
     * Trailer end reading
     * The live data of trailer end reading.
     */
    val trailerEndReading: MutableLiveData<Double> = MutableLiveData(trailerEndReadingCalc)

    /**
     * Start time
     * The start time of the delivery.
     */
    var startTime: Calendar = Calendar.getInstance()

    /**
     * End time
     * The end time of the delivery.
     */
    var endTime: Calendar = Calendar.getInstance()

    /**
     * Product list
     * The list of live data of product list.
     */
    var productList = MutableLiveData<List<String>>()

    /**
     * Product list with all info
     * The list of live data of product list with its information.
     */
    var productListWithAllInfo = MutableLiveData<List<DatabaseFuel>>()

    /**
     * Stick reading before
     * The live data of stick reading before.
     */
    var stickReadingBefore = MutableLiveData<Double>(null)

    /**
     * Stick reading after
     * The live data of stick reading after.
     */
    var stickReadingAfter = MutableLiveData<Double>(null)

    /**
     * Meter reading before
     * The live data of meter reading before.
     */
    var meterReadingBefore = MutableLiveData<Double>(null)

    /**
     * Meter reading after
     * The live data of meter reading after.
     */
    var meterReadingAfter = MutableLiveData<Double>(null)

    /**
     * Image paths
     * The mutable list of live data of image path.
     */
    var imagePaths = MutableLiveData<MutableList<String>>()

    /**
     * Navigate
     * The live data that represent the navigation state.
     */
    var navigate = MutableLiveData(false)

    /**
     * The live data that represents the loading state.
     */
    var loading = MutableLiveData(false)

    /**
     * Submit form
     * This method send the form information to the dispatcher.
     */
    fun submitForm() {
        loading.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val productId = checkIfProductsAreSame()
                    val formToSubmit = DatabaseCompletionForm(
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

    /**
     * Send product picked up message
     * This method sends the product message to the dispatcher.
     * @param productId the id of the product
     */
    private fun sendProductPickedUpMessage(productId: Int) {
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
        DeliveryStatusViewModel.sendProductFulfilledMessage(dataToSend, database)
    }

    /**
     * Check if products are same
     * This method checks if the product are same.
     * @return the new or old prodcut id
     */
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
            //if product is new
            if (isProductNew) {
                val minProductId = (productListWithAllInfo.value!!.map { it.productId }).minOrNull()
                if (minProductId != null) {
                    viewModelScope.launch {
                        //save new product to local database
                        withContext(Dispatchers.IO)
                        {
                            database.productsDao.insertFuel(
                                DatabaseFuel(
                                    minProductId - 1,
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

    /**
     * Update delivery status
     * This method updates the delivery status in the database.
     * @param tripId the id of the trip
     * @param deliveryStatus the delivery status of the trip
     */
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

    /**
     * Get products
     * This method gets the list of products from the database.
     */
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

    /**
     * Plus assign
     * This method adds new element to the list of live data.
     * @param T the type of data in the list
     * @param values the value to be added to the list
     */
    operator fun <T> MutableLiveData<ArrayList<T>>.plusAssign(values: List<T>) {
        val value = this.value ?: arrayListOf()
        value.addAll(values)
        this.value = value
    }

    /**
     * Minus assign
     * This method removes new element from the list of live data.
     * @param T the type of data in the list
     * @param values the value to be removed from the list
     */
    operator fun <T> MutableLiveData<ArrayList<T>>.minusAssign(values: List<T>) {
        val value = this.value ?: arrayListOf()
        value.removeAll(values)
        this.value = value
    }
}