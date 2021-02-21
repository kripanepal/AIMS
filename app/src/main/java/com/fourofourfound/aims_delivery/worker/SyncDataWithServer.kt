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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncDataWithServer(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params), LocationListener {

    var locationManager: LocationManager =
        appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    //will run untill doWork returns something
    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        Log.i("Location", "Running")
        withContext(Dispatchers.Main) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                this@SyncDataWithServer
            )
        }
        var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        location?.apply {
            Log.i("Location", "Running1")
            val database = getDatabase(applicationContext)
            val repository = TripListRepository(database)
            return try {
                Log.i("Location", "In try")
                var customLocation = CustomDatabaseLocation(latitude, longitude, time.toString())
                repository.refreshTrips()
                MakeNetworkCall.retrofitService.sendLocation(customLocation)
                locationManager.removeUpdates(this@SyncDataWithServer)
                Result.success()
            } catch (exception: Exception) {
                Log.i("Location", "Something went wrong")
                locationManager.removeUpdates(this@SyncDataWithServer)
                Result.failure()
            }
        }
        Log.i("Location", "Outside apply")
        return Result.retry()
    }

    fun saveLocationToDatabase() {

    }

    override fun onLocationChanged(location: Location) {

    }

}
