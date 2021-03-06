package com.fourofourfound.aims_delivery.utils

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData

/**
 * Check Internet Connection
 * This class provides a view model that contains information about
 * network connection.
 *
 * @property context the context of the current state of the application
 */
class CheckInternetConnection(val context: Context) : LiveData<Boolean>() {

    /**
     * Connectivity Manager
     * Connectivity manager object to monitor the network of the device.
     */
    private var connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * Connectivity Manager call back
     * To notify the application about the network change
     */
    private lateinit var connectivityManagerCallback: ConnectivityManager.NetworkCallback


    override fun onActive() {
        super.onActive()
        updateConnection()

        //sdk based connectivity manager
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> connectivityManager.registerDefaultNetworkCallback(
                getConnectivityManagerCallback()
            )
            else -> {
                lollipopNetworkAvailableRequest()
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
    }

    /**
     * Lollipop network available request
     * This methods makes network request object for
     * API 24 or less
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun lollipopNetworkAvailableRequest() {
        val builder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        connectivityManager.registerNetworkCallback(
            builder.build(),
            getConnectivityManagerCallback()
        )
    }

    /**
     * Get connectivity manager callback
     * This methods change the value of the live data depending
     * on the network status.
     * @return ConnectivityManager.NetworkCallback the callback that changed the live data
     * value
     */
    private fun getConnectivityManagerCallback(): ConnectivityManager.NetworkCallback {

        connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                postValue(true)
            }

            override fun onLost(network: Network) {
                postValue(false)
            }
        }
        return connectivityManagerCallback
    }

    /**
     * Update connection
     * This method updates the live data when
     * network status changes.
     */
    private fun updateConnection() {
        val activeNetwork = isNetworkAvailable(context)
        postValue(activeNetwork)
    }

    /**
     * Is network available
     *  This method checks if the network is available or not
     * @param context current state of the application
     * @return true if network is available
     */
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return false
        }
    }
}