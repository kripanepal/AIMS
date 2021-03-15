package com.fourofourfound.aims_delivery.database


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.fourofourfound.aims_delivery.database.entities.*
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.database.entities.location.DatabaseLocationWithAddress
import com.fourofourfound.aims_delivery.database.relations.DatabaseTripsWithInfo
import com.fourofourfound.aims_delivery.database.relations.SourceWithLocationAndFuel
import com.fourofourfound.aims_delivery.database.relations.TripWithLoads

@Dao
interface TripListDao {
    @Query("select * from DatabaseTrip order by completed")
    fun getTripList(): LiveData<List<DatabaseTrip>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTrips(vararg trip: DatabaseTrip)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLoads(vararg trip: DatabaseLoad)

    @Query("update DatabaseTrip set completed=:status where _id= :tripId")
    fun markTripCompleted(tripId: String, status: Boolean)

    @Query("delete from DatabaseTrip")
    fun deleteAllTrips()

    @Transaction
    @Query("select * from DatabaseTrip where _id = :tripId")
    fun getTripWithLoads(tripId: String): List<TripWithLoads>


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

    @Transaction
    @Query("select * from DatabaseSource where sourceId = 's1' ")
    suspend fun getSource(): SourceWithLocationAndFuel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrip(trip: DatabaseTripf)

    @Transaction
    @Query("select * from DatabaseTripf ")
    fun getAllTrip(): List<DatabaseTripsWithInfo>
}


@Database(
    entities = [DatabaseTrip::class, CustomDatabaseLocation::class, DatabaseLoad::class, DatabaseSource::class, DatabaseLocationWithAddress::class, DatabaseFuel::class, DatabaseSite::class, DatabaseTripf::class],
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
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}
