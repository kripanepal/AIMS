package com.fourofourfound.aims_delivery.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.fourofourfound.aims_delivery.domain.GeoCoordinates


@SuppressLint("MissingPermission")
class LocationRepository(context:Context) {
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var coordinates = MutableLiveData(GeoCoordinates(0.0, 0.0))
    var locationListenerGPS = LocationListener { location ->
        coordinates.value = GeoCoordinates(location.latitude, location.longitude)
    }


    fun removeListener() {
        coordinates.value = GeoCoordinates(0.00,0.00)
        locationManager.removeUpdates(locationListenerGPS)
    }

    fun addListener() {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            2,
            0.toFloat(), locationListenerGPS
        )
    }
}