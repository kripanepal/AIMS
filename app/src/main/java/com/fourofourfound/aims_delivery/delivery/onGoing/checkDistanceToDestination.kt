package com.fourofourfound.aims_delivery.delivery.onGoing

import android.content.Context
import android.location.Location
import android.util.Log
import com.fourofourfound.aims_delivery.domain.GeoCoordinates
import com.fourofourfound.aims_delivery.utils.CustomDialogBuilder

fun checkDistanceToDestination(source: GeoCoordinates, destination: GeoCoordinates): Int {

    val locationA = Location("point A")
    locationA.latitude = source.latitude
    locationA.longitude = source.longitude
    val locationB = Location("point B")
    locationB.latitude = destination.latitude
    locationB.longitude = destination.longitude
    return (locationA.distanceTo(locationB) * 2.23694).toInt()
}

fun showDestinationApproachingDialog(context: Context) {
    CustomDialogBuilder(
        context,
        "Sending source/site approaching message",
        "",
        "ok",
        null,
        null,
        null,
        false
    ).builder.show()
}

fun showDestinationLeaving(context: Context) {
    CustomDialogBuilder(
        context,
        "Sending source/site leaving message",
        "",
        "ok",
        null,
        null,
        null,
        false
    ).builder.show()
}
