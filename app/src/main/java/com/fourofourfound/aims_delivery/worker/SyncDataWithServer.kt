package com.fourofourfound.aims_delivery.worker

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bumptech.glide.load.HttpException
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.repository.TripListRepository
import com.fourofourfound.aims_delivery.utils.LocationUtil

class SyncDataWithServer(appContext: Context,params: WorkerParameters):
    CoroutineWorker(appContext,params) {

    companion object{
        const val WORK_NAME = "RefreshDataWorker"
    }
    //will run untill doWork returns something
    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {

        val locationProvider = LocationUtil(applicationContext)
        val database = getDatabase(applicationContext)
        val repository = TripListRepository(database)
        return try {
            repository.refreshTrips()
            locationProvider.getNewLocation()
            locationProvider.fusedLocationProviderClient.lastLocation.addOnCompleteListener { location ->
                location.result?.let { coordinate ->
                    Log.i("Location", coordinate.latitude.toString())
                }
            }

            Result.success()
        } catch (exception: HttpException) {
            Log.i("Refresh", "Something went wrong")
            Result.retry()
        }
    }

    fun saveLocationToDatabase() {

    }
}
