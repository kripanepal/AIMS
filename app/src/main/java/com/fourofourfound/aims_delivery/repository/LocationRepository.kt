package com.fourofourfound.aims_delivery.repository

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import com.fourofourfound.aims_delivery.domain.GeoCoordinates


@SuppressLint("MissingPermission")
class LocationRepository(context:Context) {
    var locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var coordinates = MutableLiveData(GeoCoordinates(0.0,0.0))
    var locationListenerGPS: LocationListener = LocationListener { location ->
        coordinates.value = GeoCoordinates(location.latitude,location.longitude)
    }

    init{
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            2,
            0.toFloat(),locationListenerGPS
        )
    }

    fun removeListener()
    {
        locationManager.removeUpdates(locationListenerGPS)
    }
}