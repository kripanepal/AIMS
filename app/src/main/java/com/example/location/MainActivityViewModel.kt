package com.example.location

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.location.Database.CustomLocationDao
import com.example.location.Database.getDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel(application: Application) : AndroidViewModel(application){
    private val myApplication = application
    private val database = getDatabase(application)

    private val _records = MutableLiveData<List<LocationRecords>>()
    val records: LiveData<List<LocationRecords>>
        get() = _records

    init {
        getAllInfo()
    }

    fun getAllInfo()
    {
        viewModelScope.launch{
            withContext(Dispatchers.IO) {
                try {
                    _records.value = database.locationDao.getAllInfo().value
                } catch (e: Exception) {

                }
            }
        }
    }

    fun saveData(location: CustomLocation, radio: String, textInput: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    var tempData = LocationRecords(location.latitude, location.longitude, radio, textInput)
                    database.locationDao.insertAll(tempData)
                    print()
                    database.locationDao.insertAll(LocationRecords(0.0, 36.345, "FIrst", "Lone"))
                } catch (e: Exception) {

                }
            }


        }
    }

    fun print () {
        getAllInfo()
        Log.i("Jlekha", _records.value?.size.toString())
    }

}