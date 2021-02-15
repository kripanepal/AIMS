package com.example.location

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.location.Database.getDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)

    val records: LiveData<List<LocationRecords>> =getData()

    private fun getData(): LiveData<List<LocationRecords>> {
        return Transformations.map(database.locationDao.getAllInfo()) {
            it
        }
    }

    fun saveData(location: CustomLocation, radio: String, textInput: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    var tempData =
                        LocationRecords(location.latitude, location.longitude, radio, textInput)
                    database.locationDao.insertAll(tempData)

                } catch (e: Exception) {
                }
            }
        }
    }

}