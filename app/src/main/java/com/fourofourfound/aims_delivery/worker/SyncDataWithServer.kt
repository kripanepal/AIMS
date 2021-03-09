package com.fourofourfound.aims_delivery.worker

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
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
    private val notificationManager =
        appContext.getSystemService(NOTIFICATION_SERVICE) as
                NotificationManager

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
        var permissionsToCheck = getLocationPermissionsToBeChecked()
        const val NOTIFICATION_ID = 101
        const val errorChannelId = "errorSendingData"
        const val errorChannelName = "Missing Requirements"
        const val errorChannelDescription =
            "Notification that is shown whenever there any missing permissions oin the background"
        const val successChannelId = "successSendingData"
        const val successChannelName = "Data synced with server"
        const val successChannelDescription =
            "Notification that is shown whenever app is synced with the server"

        const val successTitle = "Syncing with server"
        const val unKnownErrorTitle = "Cannot sync to server"
        const val unKnownErrorContentText = "Tap to open the app"
        const val unKnownErrorBigText =
            "Cannot send data to the server. Open the app to do it manually."

        const val locationErrorTitle = "Location not found"
        const val locationErrorContentText = "Tap to provide location permissions"
        const val locationErrorBigText =
            "You have disabled location permissions to this app. Please enable it to send your" +
                    "information to the dispatcher"

        const val gpsErrorTitle = "GPS not enabled"
        const val gpsErrorContentText = "Tap here to enable GPS to send your data"
        const val gpsErrorBigText =
            "You have disabled your GPS. Please turn it back on to send you information to the server"

    }

    /**
     * Do work
     * This method tries to perform the assigned work and
     * shows the appropriate notification if it fails
     * @return Result of the work
     */
    override suspend fun doWork(): Result {
        Log.i("WORKER", "Running")
        buildNotification(successTitle, null, null, null, successChannelId)

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (checkPermission(permissionsToCheck, applicationContext)) {
                setForeground(
                    ForegroundInfo(
                        NOTIFICATION_ID,
                        notification,
                        FOREGROUND_SERVICE_TYPE_LOCATION
                    )
                )
                initializeLocationManager()
                var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                location?.apply {
                    return sendLocationToServerAndUpdateTrips()
                }

                buildNotification(
                    unKnownErrorTitle,
                    unKnownErrorContentText,
                    unKnownErrorBigText,
                    null,
                    errorChannelId
                )

                notificationManager.notify(NOTIFICATION_ID, notification)
                return Result.failure()
            } else {
                Log.i("WORKER", "Missing permissions")
                val resultIntent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                buildNotification(
                    locationErrorTitle,
                    locationErrorContentText,
                    locationErrorBigText,
                    resultIntent,
                    errorChannelId
                )

                notificationManager.notify(NOTIFICATION_ID, notification)
                return Result.failure()
            }
        } else {
            Log.i("WORKER", "GPS not enabled")
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            buildNotification(
                gpsErrorTitle,
                gpsErrorContentText,
                gpsErrorBigText,
                intent,
                errorChannelId
            )
            notificationManager.notify(NOTIFICATION_ID, notification)
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
            CustomDatabaseLocation(latitude, longitude, "time")
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

    private fun buildNotification(
        title: String,
        contentText: String?,
        bigText: String?, resultIntent: Intent?, channelId: String
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(channelId)
        }

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

        notificationBuilder
            .setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setPriority(PRIORITY_DEFAULT)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setAutoCancel(true)
            .setContentText(contentText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(bigText)
            )

        resultPendingIntent?.apply {
            notificationBuilder.setContentIntent(resultPendingIntent)
        }
        notification = notificationBuilder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(id: String) {
        var name = successChannelName
        var description = successChannelDescription
        var importance = NotificationManager.IMPORTANCE_DEFAULT
        if (id === errorChannelId) {
            name = errorChannelName
            description = errorChannelDescription
            importance = NotificationManager.IMPORTANCE_HIGH
        }


        val mChannel = NotificationChannel(id, name, importance)
        mChannel.description = description
        notificationManager.createNotificationChannel(mChannel)
    }

    override fun onLocationChanged(location: Location) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

}
