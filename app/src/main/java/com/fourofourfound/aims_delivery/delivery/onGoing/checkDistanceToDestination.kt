package com.fourofourfound.aims_delivery.delivery.onGoing

import android.content.Context
import android.location.Location
import android.util.Log
import com.fourofourfound.aims_delivery.delivery.onGoing.OngoingDeliveryFragment
import com.fourofourfound.aims_delivery.domain.GeoCoordinates
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder

fun checkDistanceToDestination(source: GeoCoordinates,destination:GeoCoordinates) :Boolean{

        val locationA = Location("point A")
        locationA.latitude = source.latitude
        locationA.longitude = source.longitude
        val locationB = Location("point B")
        locationB.latitude = destination.latitude
        locationB.longitude = destination.longitude
        val distance = locationA.distanceTo(locationB)
        return distance < 1000
}

fun showDestinationApproachingDialog(context:Context)
{
        CustomDialogBuilder(context,"Sending source/site approaching message","","ok",null,null,null,false).builder.show()
}
