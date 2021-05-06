package com.fourofourfound.aims_delivery.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fourofourfound.aims_delivery.database.daos.*
import com.fourofourfound.aims_delivery.database.entities.*
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.database.utilClasses.StatusConverter

/**
 * Trip list database
 * The database that stores the information about the trip
 * @constructor Create empty Trip list database
 */
@Database
    (
    entities = [DatabaseTrailer::class,
        DatabaseTrip::class,
        DatabaseTruck::class,
        DatabaseSourceOrSite::class,
        CustomDatabaseLocation::class,
        DatabaseCompletionForm::class,
        DatabaseFuel::class,
        DatabaseLocation::class,
        BillOfLadingImages::class,
        DatabaseStatusPut::class],
    version = 2,
    exportSchema = false
)

/**
 * TipListDatabase abstract class
 */
@TypeConverters(StatusConverter::class)
abstract class TripListDatabase : RoomDatabase() {
    abstract val tripDao: TripDao
    abstract val locationDao: LocationDao
    abstract val formDao: FormDao
    abstract val destinationDao: DestinationDao
    abstract val trailerDao: TrailerDao
    abstract val productsDao: ProductsDao
    abstract val completedDeliveriesDao: CompletedDeliveriesDao
    abstract val statusPutDao: StatusPutDao
    var databaseName = ""
}

/**
 * INSTANCE
 * The instance of the database.
 */
@Volatile
private lateinit var INSTANCE: TripListDatabase

/**
 * Get database
 * This method creates the local database for the new driver after they login.
 * @param context the current context of the application
 * @param driverCode the code of the driver
 * @return the instance of the database
 */
fun getDatabase(context: Context, driverCode: String): TripListDatabase {
    var databaseForDriver = "${driverCode.trim()}-trips"
    synchronized(TripListDatabase::class.java) {
        if (!::INSTANCE.isInitialized || !INSTANCE.isOpen || INSTANCE.databaseName != databaseForDriver) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                TripListDatabase::class.java,
                databaseForDriver
            ).fallbackToDestructiveMigration()
                .build()
            INSTANCE.databaseName = databaseForDriver
        }
    }
    return INSTANCE
}