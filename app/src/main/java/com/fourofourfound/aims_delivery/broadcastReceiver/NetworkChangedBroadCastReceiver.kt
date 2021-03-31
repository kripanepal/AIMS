package com.fourofourfound.aims_delivery.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.fourofourfound.aims_delivery.database.getDatabase
import com.fourofourfound.aims_delivery.network.MakeNetworkCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Network changed broad cast receiver
 *  * This class is responsible for sending the location information that was saved
 *  in the local database because of no internet connection.
 *
 * @constructor Create empty Network changed broad cast receiver
 *
 */
class NetworkChangedBroadCastReceiver : BroadcastReceiver() {

    /**
     * On receive
     * This function is called every time a network change is detected. It includes switching
     * from wifi to mobile data or vice vera. It is also called when the cell phone loses connection
     * or when connection is reestablished
     *
     * @param context the context which registers this broadcast receiver
     * @param intent the intent that was sent to the receiver
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (isNetworkConnected(context)) {
            //launch a coroutine to in the IO thread
            GlobalScope.launch(Dispatchers.IO) {

                //get the instance of the database
                val database = getDatabase(context)

                //get saved location from the database
                database.locationDao.getSavedLocation().apply {
                    try {
                        //send the data to the dispatcher
                        MakeNetworkCall.retrofitService.sendLocation(this)

                        //delete contents of location table as only latest location is required
                        database.locationDao.deleteAllLocations()
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    /**
     * Is network connected
     * This method checks  if the device is connected to the internet and if internet is working.
     * @param context the context which called this method
     * @return true is internet connection is working else returns false
     */
    private fun isNetworkConnected(context: Context): Boolean {

        //get an instance of ConnectivityManager
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        capabilities.also {
            if (it != null) {

                //checks if either wifi or cellular data is available
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