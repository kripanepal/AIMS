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


@Database(
    entities = [DatabaseTrailer::class,
        DatabaseTrip::class,
        DatabaseTruck::class,
        DatabaseSourceOrSite::class,
        CustomDatabaseLocation::class,
        DatabaseCompletionForm::class,
        DatabaseFuel::class,
        DatabaseLocation::class,
        BillOfLadingImages::class,
        StatusTable::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(StatusConverter::class)
abstract class TripListDatabase : RoomDatabase() {
    abstract val tripDao: TripDao
    abstract val locationDao: LocationDao
    abstract val formDao: FormDao
    abstract val destinationDao: DestinationDao
    abstract val trailerDao: TrailerDao
    abstract val productsDao: ProductsDao
    abstract val completedDeliveriesDao: CompletedDeliveriesDao
    abstract val statusDao: StatusDao
    var databaseName = ""
}

@Volatile
private lateinit var INSTANCE: TripListDatabase

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