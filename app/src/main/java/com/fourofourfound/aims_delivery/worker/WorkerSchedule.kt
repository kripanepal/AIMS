package com.fourofourfound.aims_delivery.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bumptech.glide.load.HttpException
import com.fourofourfound.aims_delivery.database.TripListDatabse
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.repository.TripListRepository
import com.fourofourfound.aimsdelivery.R

class SyncDataWithServer(appContext: Context,params: WorkerParameters):
    CoroutineWorker(appContext,params) {

    companion object{
        const val WORK_NAME = "RefreshDataWorker"
    }
    //will run untill doWork returns something
    override suspend fun doWork(): Result {
        Log.i("Refresh", "Sending Location")
        val database = getDatabase(applicationContext)
        val repository = TripListRepository(database)
        return try{
            repository.refreshTrips()
            Result.success()
        }
        catch (exception: HttpException)
        {
            Log.i("Refresh", "Something went wrong")
            Result.retry()
        }
    }
}
