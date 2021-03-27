package com.fourofourfound.aims_delivery.deliveryCompletionForm


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fourofourfound.aims_delivery.domain.SourceOrSite

class DeliveryCompletionViewModel( currentSourceOrSite: SourceOrSite) : ViewModel() {
    val productDesc=  MutableLiveData(currentSourceOrSite.productDesc)
    val grossQty=  MutableLiveData(currentSourceOrSite.requestedQty.toString())
    val netQty=  MutableLiveData(currentSourceOrSite.requestedQty.toString())
    val comments=  MutableLiveData(currentSourceOrSite.fill)
    val trailerBeginReading=  MutableLiveData(currentSourceOrSite.requestedQty?.minus(100).toString())
    val trailerEndReadingCalc = Integer.parseInt(trailerBeginReading.value) - currentSourceOrSite.requestedQty!!
    val trailerEndReading=  MutableLiveData(trailerEndReadingCalc.toString())
}