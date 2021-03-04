package com.fourofourfound.aims_delivery.delivery.onGoing.maps

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.fourofourfound.aims_delivery.MainActivity
import com.fourofourfound.aimsdelivery.R

class ForegroundService : Service() {
    override fun onCreate() {
        super.onCreate()
        initChannels(this.applicationContext)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == START_ACTION) {
            val notificationIntent = Intent(this, MainActivity::class.java)
            notificationIntent.action = Intent.ACTION_MAIN
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            val pendingIntent = NavDeepLinkBuilder(this)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.delivery_navigation)
                .setDestination(R.id.navigationFragment)
                .createPendingIntent()

            val notification = NotificationCompat.Builder(this.applicationContext, CHANNEL)
                .setContentTitle("Navigation")
                .setContentText("Navigation in progress ...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setLocalOnly(true)
                .build()

            startForeground(FOREGROUND_SERVICE_ID, notification)
        } else if (intent.action == STOP_ACTION) {
            stopForeground(true)
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun initChannels(context: Context) {
        if (Build.VERSION.SDK_INT < 26) {
            return
        }
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL, "Navigation Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        var FOREGROUND_SERVICE_ID = 101
        var START_ACTION = "start navigation "
        var STOP_ACTION = "stop navigaion"
        private const val CHANNEL = "default"
    }
}

fun NavigationFragment.stopForegroundService() {
    if (foregroundServiceStarted) {
        foregroundServiceStarted = false
        val stopIntent = Intent(requireContext(), ForegroundService::class.java)
        stopIntent.action = ForegroundService.STOP_ACTION
        requireContext().startService(stopIntent)
    }
}

fun NavigationFragment.startForegroundService() {
    if (!foregroundServiceStarted) {
        foregroundServiceStarted = true
        val startIntent = Intent(requireContext(), ForegroundService::class.java)
        startIntent.action = ForegroundService.START_ACTION
        requireContext().startService(startIntent)
    }
}