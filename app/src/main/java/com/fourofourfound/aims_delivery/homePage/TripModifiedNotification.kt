package com.fourofourfound.aims_delivery.homePage

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.fourofourfound.aims_delivery.MainActivity
import com.fourofourfound.aimsdelivery.R

/**
 * Show trip modified notification
 * This method shows the notification about the trip change.
 */
fun HomePage.showTripModifiedNotification() {
    val CHANNEL_ID = "2"
     val notificationManager =
        requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
    val mChannel = NotificationChannel(
        CHANNEL_ID,
        "Trip Modified",
        NotificationManager.IMPORTANCE_HIGH
    )
    mChannel.description = "New Trips have been added or modified"
    notificationManager.createNotificationChannel(mChannel)
    val intent = PendingIntent.getActivity(
        context, 0,
        Intent(context, MainActivity::class.java), 0
    )
    val notification: Notification = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
        .setContentTitle("Trips changed")
        .setContentText("New trips are available or modified. ")
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText("Some trips might have been modified or added. Please open the app to see new details")
        )
        .setSmallIcon(R.mipmap.ic_launcher)
        .setChannelId(CHANNEL_ID)
        .setContentIntent(intent)
        .setAutoCancel(true)
        .build()
    notificationManager.notify(requireContext().getString(R.string.trip_modified_notification_id).toInt(), notification)

}