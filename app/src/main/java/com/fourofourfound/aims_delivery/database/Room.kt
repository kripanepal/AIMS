package com.fourofourfound.aims_delivery.database


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.fourofourfound.aims_delivery.database.entities.load.DatabaseLoad
import com.fourofourfound.aims_delivery.database.entities.trip.DatabaseTrip
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
    fun markTripCompleted(tripId:String,status:Boolean)

    @Query("delete from DatabaseTrip")
    fun deleteAllTrips()

    @Transaction
    @Query("select * from DatabaseTrip where _id = :tripId")
    fun getTripWithLoads(tripId:String): List<TripWithLoads>



}


@Database(entities = [DatabaseTrip::class, DatabaseLoad::class], version = 1, exportSchema = false)
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
            ).build()
        }
    }
    return INSTANCE
}
