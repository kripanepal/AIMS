package com.fourofourfound.aims_delivery.delivery.completed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fourofourfound.aims_delivery.domain.Trip

class CompletedDeliveryViewModelFactory(private val trip: Trip) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        //check if object is of type ScoreViewModel
        if (modelClass.isAssignableFrom(CompletedDeliveryViewModel::class.java)) {
            return CompletedDeliveryViewModel(trip) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}