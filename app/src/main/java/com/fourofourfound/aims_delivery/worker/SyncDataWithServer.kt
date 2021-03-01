package com.fourofourfound.aims_delivery.worker

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.network.user.MakeNetworkCall
import com.fourofourfound.aims_delivery.repository.TripListRepository
import com.fourofourfound.aims_delivery.utils.checkPermission
import com.fourofourfound.aims_delivery.utils.getPermissionsToBeChecked
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncDataWithServer(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params), LocationListener {

    var locationManager: LocationManager =
        appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val database = getDatabase(applicationContext)
    private val repository = TripListRepository(database)
    lateinit var customLocation: CustomDatabaseLocation

    companion object {
        const val WORK_NAME = "RefreshDataWorker"

        var permissionsToCheck = getPermissionsToBeChecked()
    }


    //will run untill doWork returns something

    override suspend fun doWork(): Result {
        Log.i("WORKER", "Running")
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (checkPermission(permissionsToCheck, applicationContext)) {
                initializeLocationManager()
                var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                location?.apply {
                    return sendLocationToServer()
                }
                return Result.failure()
            } else {
                ////TODO Permission not provided. Send notification to the user to provide permission for the app.
                Log.i("WORKER", "Permission not provided")
                return Result.retry()
            }
        } else {
            //TODO GPS not enabled. Send notification to the user to enable to location service on the device.
            Log.i("WORKER", "GPS not enabled")
            return Result.retry()
        }
    }

    private suspend fun Location.sendLocationToServer() = try {
        Log.i("WORKER", "SENDING LOCATION")
        customLocation =
            CustomDatabaseLocation(latitude, longitude, time.toString())
        repository.refreshTrips()
        MakeNetworkCall.retrofitService.sendLocation(customLocation)
        locationManager.removeUpdates(this@SyncDataWithServer)
        Result.success()
    } catch (exception: Exception) {
        Log.i("WORKER", "Failed")
        repository.saveLocationToDatabase(customLocation)
        locationManager.removeUpdates(this@SyncDataWithServer)
        Result.failure()
    }

    @SuppressLint("MissingPermission")
    private suspend fun initializeLocationManager() {
        withContext(Dispatchers.Main) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                this@SyncDataWithServer
            )
        }
    }


    override fun onLocationChanged(location: Location) {

    }


}
