package com.fourofourfound.aims_delivery.database


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.fourofourfound.aims_delivery.database.entities.*
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.database.relations.DatabaseTripsWithInfo

@Dao
interface TripListDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTrips(vararg trip: DatabaseTrip)


    @Query("update DatabaseTrip set status=:status where tripId= :tripId")
    fun changeTripStatus(tripId: String, status: String)

    @Query("delete from DatabaseTrip")
    fun deleteAllTrips()


    //Locations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: CustomDatabaseLocation)

    @Query("select * from CustomDatabaseLocation limit 1")
    suspend fun getSavedLocation(): CustomDatabaseLocation

    @Query("delete  from CustomDatabaseLocation")
    suspend fun deleteAllLocations()

    //Dr.Smith Json
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSources(source: List<DatabaseSource>)

    //Dr.Smith Json
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSites(site: List<DatabaseSite>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: DatabaseLocationWithAddress)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuel(fuel: DatabaseFuel)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrip(trip: DatabaseTrip)

    @Transaction
    @Query("select * from  DatabaseTrip ")
    fun getAllTrip(): LiveData<List<DatabaseTripsWithInfo>>


}


@Database(
    entities = [CustomDatabaseLocation::class, DatabaseSource::class, DatabaseLocationWithAddress::class, DatabaseFuel::class, DatabaseSite::class, DatabaseTrip::class],
    version = 1,
    exportSchema = false
)
abstract class TripListDatabse : RoomDatabase() {
    abstract val tripListDao: TripListDao
}

@Volatile
private lateinit var INSTANCE: TripListDatabse

fun getDatabase(context: Context): TripListDatabse {
    synchronized(TripListDatabse::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                TripListDatabse::class.java,
                "trips"
            )
                .build()
        }
    }
    return INSTANCE
}
