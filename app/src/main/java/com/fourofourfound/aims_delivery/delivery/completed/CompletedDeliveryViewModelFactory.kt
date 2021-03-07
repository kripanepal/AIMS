package com.fourofourfound.aims_delivery.delivery.completed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fourofourfound.aims_delivery.domain.Trip

/**
 * Completed delivery view model factory
 *Factory class which creates a viewModel for CompletedDeliveryViewModel
 * @property trip the trip which is assigned to the viewModel
 * @constructor Create empty Completed delivery view model factory
 */
class CompletedDeliveryViewModelFactory(private val trip: Trip) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        //check if object is of type ScoreViewModel
        if (modelClass.isAssignableFrom(CompletedDeliveryViewModel::class.java)) {
            return CompletedDeliveryViewModel(trip) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}