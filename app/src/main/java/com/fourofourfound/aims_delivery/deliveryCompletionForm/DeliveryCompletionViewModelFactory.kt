package com.fourofourfound.aims_delivery.deliveryCompletionForm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fourofourfound.aims_delivery.domain.SourceOrSite

/**
 * Completed delivery view model factory
 *Factory class which creates a viewModel for CompletedDeliveryViewModel
 * @property trip the trip which is assigned to the viewModel
 * @constructor Create empty Completed delivery view model factory
 */
class DeliveryCompletionViewModelFactory(private val currentSourceOrSite: SourceOrSite) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        //check if object is of type ScoreViewModel
        if (modelClass.isAssignableFrom(DeliveryCompletionViewModel::class.java)) {
            return DeliveryCompletionViewModel(currentSourceOrSite) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}