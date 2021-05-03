package com.fourofourfound.aims_delivery.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fourofourfound.aims_delivery.database.entities.*
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.database.relations.CompletedFormWithInfo
import com.fourofourfound.aims_delivery.database.relations.TripWithInfo
import com.fourofourfound.aims_delivery.database.utilClasses.ProductPickedUpData
import com.fourofourfound.aims_delivery.network.ProductPickupResponse
import com.fourofourfound.aims_delivery.utils.DeliveryStatusEnum

@Dao
interface LocationDao {
    //Locations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: CustomDatabaseLocation)

    @Query("select * from CustomDatabaseLocation limit 1")
    suspend fun getSavedLocation(): CustomDatabaseLocation

    @Query("delete  from CustomDatabaseLocation")
    suspend fun deleteAllLocations()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: DatabaseLocation)
}

@Dao
interface FormDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFormData(formData: DatabaseCompletionForm)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addBillOfLadingImages(images: List<BillOfLadingImages>)
}

@Dao
interface TrailerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrailer(trailer: DatabaseTrailer)

    @Query("update DatabaseTrailer set fuelQuantity =:fuelQuantity where trailerId =:trailerId")
    suspend fun updateTrailerFuel(trailerId: Int, fuelQuantity: Double)
}

@Dao
interface DestinationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDestination(vararg siteOrSource: DatabaseSourceOrSite)

    @Query("update DatabaseSourceOrSite set deliveryStatus = :deliveryStatus where seqNum=:seqNum and tripId =:tripId")
    suspend fun updateDeliveryStatus(tripId: Int, seqNum: Int, deliveryStatus: DeliveryStatusEnum)

    @Query("select * from  DatabaseSourceOrSite  where tripId=:tripId and seqNum=:seqNum limit 1")
    fun getDestination(tripId: Int, seqNum: Int): DatabaseSourceOrSite

    @Query("select count(*) from DatabaseSourceOrSite where deliveryStatus = 2")
    fun getTotalDestinations(): Int

}

@Dao
interface TripDao {
    @Query("update DatabaseTrip set deliveryStatus=:deliveryStatus where tripId= :tripId")
    fun changeTripStatus(tripId: Int, deliveryStatus: DeliveryStatusEnum)

    @Query("select tripId from DatabaseTrip where deliveryStatus = 2")
    fun getCompletedTripsId():List<Int>

    @Query("delete from DatabaseTrip")
    fun deleteAllTrips()

    @Query("DELETE FROM DatabaseTrip WHERE tripId IN (:ids) and deliveryStatus not in (0,2) ")
    fun deleteTripById(ids: List<Int>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: DatabaseTrip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTruck(truck: DatabaseTruck)


    @Transaction
    @Query("select * from  DatabaseTrip order by deliveryStatus desc ")
    fun getAllTrip(): LiveData<List<TripWithInfo>>

    @Transaction
    @Query("select * from  DatabaseTrip order by deliveryStatus desc ")
    fun getAllTripsOneTime(): List<TripWithInfo>


    @Query("select * from  DatabaseTrip  where tripId=:tripId")
    fun getTripById(tripId: Int): DatabaseTrip?

    @Query("select count(*) from DatabaseTrip where deliveryStatus = 2")
    fun getTotalCompletedTrips(): Int

}

@Dao
interface ProductsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuel(fuel: DatabaseFuel)

    @Query("select * from DatabaseFuel")
    suspend fun getProducts(): List<DatabaseFuel>
}


@Dao
interface CompletedDeliveriesDao {

    @Transaction
    @Query("select * from DatabaseCompletionForm where tripId =:tripId")
    fun getDetails( tripId:Int): List<CompletedFormWithInfo>

    @Transaction
    @Query("select * from DatabaseCompletionForm where tripId =:tripId and seqNo =:seqNo")
    fun getDetailsForDestination(tripId: Int, seqNo: Int): List<CompletedFormWithInfo>

    @Query("select tripId,billOfLadingNumber,endTime,grossQty,netQty,startTime,sourceOrSiteId as sourceId ,driverId as driverCode, productDelivered as productId from DatabaseCompletionForm where wayPointType='Source' and dataSent = 0")
    fun getUnsentProductPickedUp(): List<ProductPickedUpData>

    @Query("update DatabaseCompletionForm set dataSent = 1 where  sourceOrSiteId=:id")
    fun updateFormSentStatus(id:Int)



}


@Dao
interface StatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStatusTable( statusList:List<StatusTable>)

    @Query("select * from StatusTable")
    fun getStatusTable(): List<StatusTable>
}

@Dao
interface StatusPutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPutData( toPut:DatabaseStatusPut)

   @Delete
    fun deletePutData( toDelete:DatabaseStatusPut)

    @Query("select * from DatabaseStatusPut")
    fun getAllUnsentData():List<DatabaseStatusPut>


}






