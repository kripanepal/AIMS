package com.fourofourfound.aims_delivery.settings.mapDownload

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.here.android.mpa.odml.MapPackage

/**
 * Map download view model
 * This view model is used by the Map Fragment to store the map information
 * @constructor
 */
class MapDownloadViewModel : ViewModel() {

    /**
     * _package list
     * This holds the list of the map package and encapsulates the data.
     */
    private val _packageList = MutableLiveData<List<MapPackage>>()
    val packageList: MutableLiveData<List<MapPackage>>
        get() = _packageList

    /**
     * Set package list
     * This methods sets the map package to the packageList
     * @param newPackageList
     */
    fun setPackageList(newPackageList: List<MapPackage>) {
        _packageList.value = newPackageList
    }

    /**
     * Loading
     * Shows the loading animation when required.
     */
    var loading = MutableLiveData(true)

    /**
     * Map downloading
     * The live data that checks if the map has been downloaded or not.
     */
    var mapDownloading = MutableLiveData(false)

    /**
     * Map downloading percentage
     * The live data that holds the map download percentage.
     */
    var mapDownloadingPercentage = MutableLiveData(0)

    /**
     * Display messages
     * This holds the message to be displayed when certain action is triggered.
     */
    var displayMessages = MutableLiveData<String>();

    /**
     * Currently downloading state
     * The current downloading state of the map.
     */
    var currentlyDownloadingState = ""
}