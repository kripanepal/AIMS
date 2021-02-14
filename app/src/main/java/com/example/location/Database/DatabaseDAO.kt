package com.example.location.Database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.location.LocationRecords

@Dao
interface CustomLocationDao  {

    @Query("SELECT * FROM LocationRecords")
     fun getAllInfo(): LiveData<List<LocationRecords>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
     fun insertAll(vararg info: LocationRecords)
}