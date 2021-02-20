package com.fourofourfound.aims_delivery.utils

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.fourofourfound.aims_delivery.worker.SyncDataWithServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomWorkManager(var context: Context) {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    fun sendLocationAndUpdateTrips() {
        applicationScope.launch {
            var repeatingRequest = OneTimeWorkRequestBuilder<SyncDataWithServer>()
                //.setInitialDelay(10, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueue(repeatingRequest)
        }
    }
}