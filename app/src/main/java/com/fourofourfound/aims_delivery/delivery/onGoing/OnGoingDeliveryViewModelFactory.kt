package com.fourofourfound.aims_delivery.delivery.onGoing

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fourofourfound.aims_delivery.domain.SourceOrSite

/**
 * Completed delivery view model factory
 *Factory class which creates a viewModel for CompletedDeliveryViewModel
 * @property trip the trip which is assigned to the viewModel
 * @constructor Create empty Completed delivery view model factory
 */
class OnGoingDeliveryViewModelFactory(private val application: Application, private val currentSourceOrSite: SourceOrSite) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        //check if object is of type ScoreViewModel
        if (modelClass.isAssignableFrom(OngoingDeliveryViewModel::class.java)) {
            return OngoingDeliveryViewModel(application, currentSourceOrSite) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}