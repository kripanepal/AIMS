package com.fourofourfound.aims_delivery.settings.mapDownload

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.here.android.mpa.odml.MapPackage

class MapDownloadViewModel : ViewModel() {


    private val _packageList = MutableLiveData<List<MapPackage>>()
    val packageList: MutableLiveData<List<MapPackage>>
        get() = _packageList

    fun setPackageList(newPackageList: List<MapPackage>) {
        _packageList.value = newPackageList
    }

    var loading = MutableLiveData<Boolean>()


}