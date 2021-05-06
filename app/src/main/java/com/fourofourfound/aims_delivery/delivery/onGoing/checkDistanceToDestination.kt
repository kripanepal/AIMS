package com.fourofourfound.aims_delivery.delivery.onGoing

import android.location.Location
import com.fourofourfound.aims_delivery.domain.GeoCoordinates

/**
 * Check distance to destination
 * This method checks the distance from source to the destination
 * @param source the geo coordinates of source
 * @param destination the geo coordinates of destination
 * @return the distance between source and destination
 */
fun checkDistanceToDestination(source: GeoCoordinates, destination: GeoCoordinates): Int {

    val locationA = Location("point A")
    locationA.latitude = source.latitude
    locationA.longitude = source.longitude
    val locationB = Location("point B")
    locationB.latitude = destination.latitude
    locationB.longitude = destination.longitude
    return (locationA.distanceTo(locationB) * 2.23694).toInt()
}

