package com.fourofourfound.aims_delivery.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.network.user.MakeNetworkCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class NetworkChangedBroadCastReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        Log.i("Sending", "RECEIVED")

        if (isNetworkConnected(context)) {
            Log.i("Sending", "CONNECTED")
            GlobalScope.launch(Dispatchers.IO) {
                val database = getDatabase(context)
                database.tripListDao.getSavedLocation().apply {
                    try {
                        MakeNetworkCall.retrofitService.sendLocation(this)
                        database.tripListDao.deleteAllLocations()

                    } catch (e: Exception) {

                    }
                }
            }

        }

    }

    private fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        capabilities.also {
            if (it != null) {
                if (it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    return true
                else if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                }
            }
        }
        return false
    }

}