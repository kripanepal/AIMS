package com.fourofourfound.aims_delivery.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import androidx.lifecycle.MutableLiveData
import com.fourofourfound.aims_delivery.domain.GeoCoordinates

/**
 * Location repository
 * This class provides the current location of the user to the application.
 * @constructor
 *
 * @param context the current context of the application.
 */
@SuppressLint("MissingPermission")
class LocationRepository(context: Context) {

    /**
     * Location manager
     * This provides access to the system location services.
     */
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    /**
     * Coordinates
     * This live data provides the current gro coordinates of the driver.
     */
    var coordinates = MutableLiveData(GeoCoordinates(0.0, 0.0))

    /**
     * Location listener GPS
     * This is used for receiving notifications when the device location has changed.
     */
    var locationListenerGPS = LocationListener { location ->
        coordinates.value = GeoCoordinates(location.latitude, location.longitude)
    }

    /**
     * Remove listener
     * This method removes all location updates for the specified LocationListener.
     */
    fun removeListener() {
        coordinates.value = GeoCoordinates(0.00, 0.00)
        locationManager.removeUpdates(locationListenerGPS)
    }

    /**
     * Add listener
     * This method provides location to the specified LocationListener.
     */
    fun addListener() {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            2,
            0.toFloat(), locationListenerGPS
        )
    }
}