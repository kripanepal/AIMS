package com.fourofourfound.aims_delivery.database


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.fourofourfound.aims_delivery.database.entities.DatabaseTrip

@Dao
interface TripListDao {
    @Query("select * from DatabaseTrip")
    fun getTripList(): LiveData<List<DatabaseTrip>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg trip: DatabaseTrip)

    @Query("update DatabaseTrip set completed=:status where _id= :tripId")
    fun markTripCompleted(tripId:String,status:Boolean)

}


@Database(entities = [DatabaseTrip::class], version = 1)
abstract class TripListDatabse : RoomDatabase() {
    abstract val tripListDao: TripListDao
}

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
