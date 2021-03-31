package com.fourofourfound.aims_delivery.database


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.fourofourfound.aims_delivery.database.entities.*
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.database.relations.TripWithInfo

@Dao
interface TripListDao {
    @Query("update DatabaseTrip set status=:status where tripId= :tripId")
    fun changeTripStatus(tripId: Int, status: String)

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: DatabaseTrip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTruck(truck: DatabaseTruck)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrailer(trailer: DatabaseTrailer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuel(fuel: DatabaseFuel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: DatabaseLocation)


    @Transaction
    @Query("select * from  DatabaseTrip ")
    fun getAllTrip(): LiveData<List<TripWithInfo>>


    @Query("select * from  DatabaseTrip  where tripId=:tripId ")
    fun getTripById(tripId: Int): DatabaseTrip?

    @Insert
    fun insertFormData(formData: DatabaseForm)

    @Query("update DatabaseSourceOrSite set status = 'COMPLETED' where seqNum=:seqNum and tripId =:tripId")
    fun markDeliveryCompleted(tripId: Int, seqNum: Int)

    @Query("select * from  DatabaseSourceOrSite  where tripId=:tripId and seqNum=:seqNum limit 1")
    fun getSourceOrSite(tripId: Int, seqNum: Int): DatabaseSourceOrSite



}


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
abstract class TripListDatabase : RoomDatabase() {
    abstract val tripListDao: TripListDao
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
