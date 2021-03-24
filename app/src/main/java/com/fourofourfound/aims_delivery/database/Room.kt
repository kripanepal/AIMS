package com.fourofourfound.aims_delivery.database


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.fourofourfound.aims_delivery.database.entities.DatabaseSourceOrSite
import com.fourofourfound.aims_delivery.database.entities.DatabaseTrailer
import com.fourofourfound.aims_delivery.database.entities.DatabaseTrip
import com.fourofourfound.aims_delivery.database.entities.DatabaseTruck
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.database.relations.DatabaseTripsWithInfo

@Dao
interface TripListDao {
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
    suspend fun insertSitesOrSource(vararg siteOrSource: DatabaseSourceOrSite)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrip(trip: DatabaseTrip)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTruck(truck: DatabaseTruck)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrailer(trailer: DatabaseTrailer)

    @Transaction
    @Query("select * from  DatabaseTrip ")
    fun getAllTrip(): LiveData<List<DatabaseTripsWithInfo>>


}


@Database(
    entities = [DatabaseTrailer::class, DatabaseTrip::class, DatabaseTruck::class, DatabaseSourceOrSite::class, CustomDatabaseLocation::class],
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
