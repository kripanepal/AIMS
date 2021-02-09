package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*





class MainActivity : AppCompatActivity() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    private val PERMISSION_REQUEST = 50


    private var radioSelected: String = "NONE"
    private var textEntered: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        button.setOnClickListener {
            requestPermission()
            getLastLocation()
            textEntered = text_view.text.toString()
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            (findViewById<RadioButton>(checkedId).text as String).let {
                if (!it.isNullOrEmpty()) {
                    radioSelected = it
                }

            }
        }

    }


    //get the last known location
    private fun getLastLocation(){
        if(checkPermission()){
            if(isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result
                    if(location == null){
                        getNewLocation()
                    }else{
                        location_text.text =
                            "Your Current Location Latitude is: " + location.latitude + "and longitude is " + location.longitude + "\n\n selected option " + radioSelected + "\n\nText entered " + textEntered
                    }
                }
            }else{
                Toast.makeText(this, "Please enable your gps", Toast.LENGTH_LONG).show()
            }
        }else{
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object: LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation
        }
    }

    //Checks the users permission
    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    //asks for the user permission
    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST)
    }

    //checks if device location service is enabled
    private fun isLocationEnabled(): Boolean{
        var locationManager= getSystemService(Context.LOCATION_SERVICE) as LocationManager
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