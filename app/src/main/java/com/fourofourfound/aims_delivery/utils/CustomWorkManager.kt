package com.fourofourfound.aims_delivery.utils

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fourofourfound.aims_delivery.worker.SyncDataWithServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CustomWorkManager(var context: Context) {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    fun scheduleWork() {
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
}