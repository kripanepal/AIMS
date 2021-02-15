package com.example.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*

class MyLocationProvider(var context: Context) {
    //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
    var fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
        context)
    lateinit var locationRequest: LocationRequest
    private val PERMISSION_REQUEST = 50

   //var location: Location? = null
    //lateinit var customLocation: CustomLocation
    //get the last known location
    fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                        getNewLocation()
                }
            }else{

            }
        } else {
            requestPermission()
        }
    }

    @SuppressLint("MissingPermission")
    fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation
        }
    }

    //Checks the users permission
    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    //asks for the user permission
    fun requestPermission() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST
        )
    }

    //checks if device location service is enabled
    fun isLocationEnabled(): Boolean {
        var locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

//    private fun getCityName(lat: Double, long: Double): String {
//        var cityName = ""
//        var geoCoder = Geocoder(this, Locale.getDefault())
//        var address: MutableList<Address> = geoCoder.getFromLocation(lat, long, 1)
//
//        cityName = address[0].locality
//        return cityName
//    }

//    private fun getCountryName(lat: Double, long: Double): String {
//        var countryName = ""
//        var geoCoder = Geocoder(this, Locale.getDefault())
//        var address: MutableList<Address> = geoCoder.getFromLocation(lat, long, 1)
//
//        countryName = address[0].countryName
//        return countryName
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        //checks the permission result
//        if(requestCode == PERMISSION_REQUEST){
//            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                Log.i("Answer", "there is a permission")
//            }
//        }
//    }
}