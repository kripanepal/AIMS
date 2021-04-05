package com.fourofourfound.aims_delivery.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fourofourfound.aims_delivery.database.entities.*
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.database.relations.TripWithInfo
import com.fourofourfound.aims_delivery.utils.StatusEnum

@Dao
interface LocationDao {
    //Locations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: CustomDatabaseLocation)

    @Query("select * from CustomDatabaseLocation limit 1")
    suspend fun getSavedLocation(): CustomDatabaseLocation

    @Query("delete  from CustomDatabaseLocation")
    suspend fun deleteAllLocations()
}

@Dao
interface FormDao {

    @Insert
    fun insertFormData(formData: DatabaseForm)
}

@Dao
interface DestinationDao {

    //Dr.Smith Json
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDestination(vararg siteOrSource: DatabaseSourceOrSite)

    @Query("update DatabaseSourceOrSite set status = :status where seqNum=:seqNum and tripId =:tripId")
    fun updateDeliveryStatus(tripId: Int, seqNum: Int, status: StatusEnum)

    @Query("select * from  DatabaseSourceOrSite  where tripId=:tripId and seqNum=:seqNum limit 1")
    fun getDestination(tripId: Int, seqNum: Int): DatabaseSourceOrSite


}

@Dao
interface TripDao {
    @Query("update DatabaseTrip set status=:status where tripId= :tripId")
    fun changeTripStatus(tripId: Int, status: StatusEnum)

    @Query("delete from DatabaseTrip")
    fun deleteAllTrips()


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
    @Query("select * from  DatabaseTrip order by status desc ")
    fun getAllTrip(): LiveData<List<TripWithInfo>>


    @Query("select * from  DatabaseTrip  where tripId=:tripId")
    fun getTripById(tripId: Int): DatabaseTrip?

}
