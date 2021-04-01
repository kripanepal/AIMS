package com.fourofourfound.aims_delivery.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fourofourfound.aims_delivery.worker.SyncDataWithServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Custom work manager
 * This class creates a work manager object to refresh trip list,
 * send location coordinates on certain intervals.
 * @property context  the current state of the application
 * @constructor Create empty Custom work manager
 */
class CustomWorkManager(var context: Context) {

    /**
     * Application scope
     * Coroutine scope that runs in the background
     */
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    /**
     * Send location and update trips
     * This method runs in certain intervals to perform the
     * work assigned in the background
     */
    fun sendLocationAndUpdateTrips() {
        //Schedule a work in 15 minutes interval
        applicationScope.launch {
            var repeatingRequest = PeriodicWorkRequestBuilder<SyncDataWithServer>(
                15,
                TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SyncDataWithServer.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                repeatingRequest
            )
        }
    }

    /**
     * Send location onetime
     * This method runs once to perform the
     * work assigned in the background
     */
    fun sendLocationOnetime() {
        //Runs a work one time for testing only
        applicationScope.launch {
            var repeatingRequest = OneTimeWorkRequestBuilder<SyncDataWithServer>()
                .setInitialDelay(15, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueue(repeatingRequest)
        }
    }
}