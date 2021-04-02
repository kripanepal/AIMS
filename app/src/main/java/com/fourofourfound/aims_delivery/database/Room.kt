package com.fourofourfound.aims_delivery.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fourofourfound.aims_delivery.database.daos.DestinationDao
import com.fourofourfound.aims_delivery.database.daos.FormDao
import com.fourofourfound.aims_delivery.database.daos.LocationDao
import com.fourofourfound.aims_delivery.database.daos.TripDao
import com.fourofourfound.aims_delivery.database.entities.*
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.database.utilClasses.StatusConverter


@Database(
    entities = [DatabaseTrailer::class,
        DatabaseTrip::class,
        DatabaseTruck::class,
        DatabaseSourceOrSite::class,
        CustomDatabaseLocation::class,
        DatabaseForm::class,
        DatabaseFuel::class,
        DatabaseLocation::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StatusConverter::class)
abstract class TripListDatabase : RoomDatabase() {
    abstract val tripDao: TripDao
    abstract val locationDao: LocationDao
    abstract val formDao: FormDao
    abstract val destinationDao: DestinationDao

}

@Volatile
private lateinit var INSTANCE: TripListDatabase

fun getDatabase(context: Context): TripListDatabase {
    synchronized(TripListDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                TripListDatabase::class.java,
                "trips"
            )
                .build()
        }
    }
    return INSTANCE
}