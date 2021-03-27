package com.fourofourfound.aims_delivery.database


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.fourofourfound.aims_delivery.database.entities.*
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: DatabaseTrip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTruck(truck: DatabaseTruck)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrailer(trailer: DatabaseTrailer)

    @Transaction
    @Query("select * from  DatabaseTrip ")
    fun getAllTrip(): LiveData<List<DatabaseTripsWithInfo>>

    @Query("select distinct productDesc from  DatabaseSourceOrSite where tripId=:tripId ")
    fun getAllProductsForTrip(tripId: String): List<String>

    @Query("select destinationName from DatabaseSourceOrSite where productDesc =:productDesc and wayPointTypeDescription='Source'and tripId =:tripId")
    fun getFuelSource(productDesc: String, tripId: String): String

    @Query("select count(wayPointTypeDescription) from DatabaseSourceOrSite where productDesc =:productDesc and wayPointTypeDescription='Site Container' and tripId =:tripId")
    fun getSiteCount(productDesc: String, tripId: String): Int

    @Query("select * from  DatabaseTrip  where tripId=:tripId ")
    fun getTripById(tripId: String): DatabaseTrip?

    @Insert
    fun insertFormData(formData: DatabaseForm)

    @Query("update DatabaseSourceOrSite set status = 'COMPLETED' where seqNum=:seqNum and tripId =:tripId")
    fun markDeliveryCompleted(tripId: String, seqNum: Int)

    @Query("select * from  DatabaseSourceOrSite  where tripId=:tripId and seqNum=:seqNum limit 1")
    fun getSourceOrSite(tripId: String, seqNum: Int): DatabaseSourceOrSite


}


@Database(
    entities = [DatabaseTrailer::class, DatabaseTrip::class, DatabaseTruck::class, DatabaseSourceOrSite::class, CustomDatabaseLocation::class, DatabaseForm::class],
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
