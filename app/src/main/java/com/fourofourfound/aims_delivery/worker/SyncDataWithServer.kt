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
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.repository.TripListRepository
import com.fourofourfound.aims_delivery.utils.*
import com.fourofourfound.aimsdelivery.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*


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

    /**
     * Location manager
     * The location manager that is responsible for getting the user current location
     */
    var locationManager: LocationManager =
        appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    /**
     * Database
     * The database of the currently logged in driver
     */
    val database = getDatabaseForDriver(applicationContext)

    /**
     * Repository
     * The repository which contains information about the trip and destinations
     */
    private val repository = TripListRepository(database)

    /**
     * Custom location
     * The location class that holds the current location coordinates and timestamp of the driver
     */
    lateinit var customLocation: CustomDatabaseLocation

    /**
     * Notification builder
     * The builder that builds the notification
     */
    lateinit var notificationBuilder: NotificationCompat.Builder

    /**
     * Notification
     * The notification the is shown when the required permisson are disabled
     */
    lateinit var notification: Notification

    /**
     * Notification manager
     * The manager that is responsible for showing and hiding notifications
     */
    private val notificationManager =
        appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

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
        Log.i("WORKER-WORK", "Running")

        //TODO remove this as it should run in the defined interval. Just for presentation
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            CustomWorkManager(applicationContext).apply {
            sendLocationAndUpdateTrips()
                sendLocationOnetime()
            }
        }, 60000)


        buildNotification(successTitle, null, null, null, successChannelId)

        //GPS available
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //location permission provided
            if (checkPermission(permissionsToCheck, applicationContext)) {
                initializeLocationManager()
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                location?.apply {
                    setForeground(
                        ForegroundInfo(
                            NOTIFICATION_ID,
                            notification,
                            FOREGROUND_SERVICE_TYPE_LOCATION
                        )
                    )
                    return sendLocationToServerAndUpdateTrips()
                }
                Log.i("WORKER-WORK", "Missing location")
                return showMissingPermissionNotification()
            } else {
                return showMissingPermissionNotification()
            }
        } else {
            Log.i("WORKER-WORK", "GPS not enabled")
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            buildNotification(
                gpsErrorTitle,
                gpsErrorContentText,
                gpsErrorBigText,
                intent,
                errorChannelId
            )
            notificationManager.notify(NOTIFICATION_ID, notification)
            Log.i("WORKER-WORK", "Missing permissions")
            return Result.failure()
        }

    }

    /**
     * Show missing permission notification
     * This method shows notification if location permission is missing
     * @return the result of the work manager (failed)
     */
    private fun showMissingPermissionNotification(): Result {
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

    /**
     * Send location to server and update trips
     * This method sends the location information to the server
     * and gets updated trip from the server if any
     */
    private suspend fun Location.sendLocationToServerAndUpdateTrips() = try {

        customLocation =
            CustomDatabaseLocation(latitude, longitude, getDateAndTime(Calendar.getInstance()))
        Log.i("WORKER-WORK", "SENDING LOCATION $customLocation")
        Log.i("WORKER-WORK", "Refreshing trips")
        var code: String
        CustomSharedPreferences(applicationContext).apply {
            code = getEncryptedPreference("driverCode")
        }
        repository.refreshTrips(code)
        locationManager.removeUpdates(this@SyncDataWithServer)
        Log.i("WORKER-WORK", "SUCCESSFUL")
        Result.success()
    } catch (exception: Exception) {
        Log.i("WORKER-WORK", "Failed")
        Log.i("WORKER-WORK", exception.message.toString())
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

    /**
     * Build notification
     * This method is responsible for building a notification
     * @param title the title of the notification
     * @param contentText the message of the notification
     * @param bigText the description of the notification
     * @param resultIntent the intent that is to be handled when notification is clicked
     * @param channelId the channel id for the notification
     */
    private fun buildNotification(
        title: String,
        contentText: String?,
        bigText: String?, resultIntent: Intent?, channelId: String
    ) {
        createNotificationChannel(channelId)
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

        //builds a notification
        notificationBuilder
            .setOngoing(false)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setPriority(PRIORITY_DEFAULT)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setAutoCancel(true)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
        resultPendingIntent?.apply {
            notificationBuilder.setContentIntent(resultPendingIntent)
        }
        notification = notificationBuilder.build()
    }

    /**
     * Create notification channel
     * This methods creates the notification channel
     * @param id the channel id
     */
    private fun createNotificationChannel(id: String) {
        var name = successChannelName
        var description = successChannelDescription
        var importance = NotificationManager.IMPORTANCE_LOW
        if (id === errorChannelId) {
            name = errorChannelName
            description = errorChannelDescription
        }
        val mChannel = NotificationChannel(id, name, importance)
        mChannel.description = description
        notificationManager.createNotificationChannel(mChannel)
    }

    override fun onLocationChanged(location: Location) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

}
