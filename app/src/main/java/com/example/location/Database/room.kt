package com.example.location.Database

import android.content.Context
import androidx.room.*
import com.example.location.LocationRecords

@Database(entities = [LocationRecords::class], version = 1)
abstract class CustomLocationDatabase : RoomDatabase() {
    abstract val locationDao: CustomLocationDao
}

private lateinit var INSTANCE: CustomLocationDatabase

fun getDatabase(context: Context): CustomLocationDatabase {
    synchronized(CustomLocationDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                CustomLocationDatabase::class.java,
                "trips"
            ).build()
        }
    }
    return INSTANCE
}