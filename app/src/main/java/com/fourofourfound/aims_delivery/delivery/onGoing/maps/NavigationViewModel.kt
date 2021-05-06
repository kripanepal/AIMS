package com.fourofourfound.aims_delivery.delivery.onGoing.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fourofourfound.aimsdelivery.R

/**
 * Navigation view model
 * This view model is used by the Navigation Fragment to store the information about navigation.
 * @constructor Create empty Navigation view model
 */
class NavigationViewModel : ViewModel() {
    /**
     * Next maneuver road name
     */
    var nextManeuverRoadName = MutableLiveData("")

    /**
     * Next maneuver direction
     */
    var nextManeuverDirection = MutableLiveData("")

    /**
     * Next maneuver arrow
     */
    var nextManeuverArrow = MutableLiveData(R.drawable.go_straight)
}