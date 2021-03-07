package com.fourofourfound.aims_delivery.worker

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.network.MakeNetworkCall
import com.fourofourfound.aims_delivery.repository.TripListRepository
import com.fourofourfound.aims_delivery.utils.checkPermission
import com.fourofourfound.aims_delivery.utils.getLocationPermissionsToBeChecked
import com.fourofourfound.aimsdelivery.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * Sync data with server
 * This class is responsible for syncing data with server to
 * update local database and inform the server about current location
 * @constructor
 *
 * @param appContext current state of the application
 * @param params parameters for a coroutine worker
 */
class SyncDataWithServer(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params), LocationListener {

    var locationManager: LocationManager =
        appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val database = getDatabase(applicationContext)
    private val repository = TripListRepository(database)
    lateinit var customLocation: CustomDatabaseLocation
    lateinit var notificationBuilder: NotificationCompat.Builder
    lateinit var notification: Notification

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
        var permissionsToCheck = getLocationPermissionsToBeChecked()
        const val NOTIFICATION_ID = 101

    }

    /**
     * Do work
     * This method tries to perform the assigned work and
     * shows the appropriate notification if it fails
     * @return Result of the work
     */
    override suspend fun doWork(): Result {
        Log.i("WORKER", "Running")
        setForeground(startForeground("Sending", null))
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (checkPermission(permissionsToCheck, applicationContext)) {
                initializeLocationManager()
                var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                location?.apply {
                    return sendLocationToServerAndUpdateTrips()
                }
                return Result.failure()
            } else {
                Log.i("WORKER", "Permission not provided")
                val resultIntent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                setForeground(startForeground("Permission not provided.", resultIntent))
                return Result.failure()
            }
        } else {
            Log.i("WORKER", "GPS not enabled")
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.putExtra("enabled", true)
            setForeground(startForeground("GPS not enabled", intent))
            return Result.failure()
        }
    }

    /**
     * Send location to server and update trips
     * This method sends the location information to the server
     * and gets updated trip from the server if any
     */
    private suspend fun Location.sendLocationToServerAndUpdateTrips() = try {
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

    /**
     * Initialize location manager
     * This method initializes the location manager
     */
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

    private fun startForeground(title: String, resultIntent: Intent?): ForegroundInfo {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else ""

        var resultPendingIntent: PendingIntent? = null
        resultIntent?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (resultIntent.action == ACTION_APPLICATION_DETAILS_SETTINGS) {
                val uri: Uri = Uri.fromParts("package", applicationContext.packageName, null)
                data = uri
            }

            resultPendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)

        var toBuildNotification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setPriority(PRIORITY_DEFAULT)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setAutoCancel(false)
            .setContentIntent(resultPendingIntent)

        resultPendingIntent?.apply {
            toBuildNotification.setContentIntent(resultPendingIntent)
        }
        notification = toBuildNotification.build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }


    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

}
