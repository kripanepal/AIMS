package com.fourofourfound.aims_delivery.delivery.onGoing.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fourofourfound.aims_delivery.domain.SourceOrSite
import com.fourofourfound.aimsdelivery.R

class NavigationViewModel : ViewModel() {
    var nextManeuverRoadName = MutableLiveData("")
    var nextManeuverDirection = MutableLiveData("")
    var nextManeuverArrow = MutableLiveData(R.drawable.go_straight)


}