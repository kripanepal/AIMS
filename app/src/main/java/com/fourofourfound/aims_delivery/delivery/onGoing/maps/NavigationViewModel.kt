package com.fourofourfound.aims_delivery.delivery.onGoing.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NavigationViewModel : ViewModel() {
    var nextManeuverRoadName = MutableLiveData("")
    var nextManeuverDirection = MutableLiveData("")
}