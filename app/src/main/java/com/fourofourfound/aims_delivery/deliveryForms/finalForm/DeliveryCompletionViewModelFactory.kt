package com.fourofourfound.aims_delivery.deliveryForms.finalForm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fourofourfound.aims_delivery.domain.SourceOrSite

/**
 * Completed delivery view model factory
 * Factory class which creates a viewModel for CompletedDeliveryViewModel
 * @property application Application Base class for maintaining global application state.
 * @property currentSourceOrSite SourceOrSite current destination
 * @constructor Create empty Delivery completion view model factory
 */
class DeliveryCompletionViewModelFactory(
    private val application: Application,
    private val currentSourceOrSite: SourceOrSite
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        //check if object is of type ScoreViewModel
        if (modelClass.isAssignableFrom(DeliveryCompletionViewModel::class.java)) {
            return DeliveryCompletionViewModel(application, currentSourceOrSite) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}