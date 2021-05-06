package com.fourofourfound.aims_delivery.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.fourofourfound.aims_delivery.database.entities.*
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.database.relations.CompletedFormWithInfo
import com.fourofourfound.aims_delivery.database.relations.TripWithInfo
import com.fourofourfound.aims_delivery.database.utilClasses.ProductData
import com.fourofourfound.aims_delivery.utils.DeliveryStatusEnum

/**
 * Location dao
 * Data access object to manipulate location of the user.
 * @constructor Create empty Location dao
 */
@Dao
interface LocationDao {
    /**
     * Insert location
     * Insert unsent location to the database.
     * @param location the location to be saved
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: CustomDatabaseLocation)

    /**
     * Get saved location
     * Gets the location from the database.
     * @return the location
     */
    @Query("select * from CustomDatabaseLocation limit 1")
    suspend fun getSavedLocation(): CustomDatabaseLocation

    /**
     * Delete all locations
     * Deletes all location from the database
     */
    @Query("delete  from CustomDatabaseLocation")
    suspend fun deleteAllLocations()

    /**
     * Insert location
     * Insert the location of the destination to the database.
     * @param location the location to be saved
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: DatabaseLocation)
}

/**
 * Form dao
 * Data access object to manipulate the form.
 * @constructor
 */
@Dao
interface FormDao {

    /**
     * Insert form data
     * This method inserts form data.
     * @param formData the form data to be saved
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFormData(formData: DatabaseCompletionForm)

    /**
     * Add bill of lading images
     * This method adds the list of bill of lading images path.
     * @param images
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addBillOfLadingImages(images: List<BillOfLadingImages>)
}

/**
 * Trailer dao
 * Data access object to manipulate trailer information.
 * @constructor Create empty Trailer dao
 */
@Dao
interface TrailerDao {
    /**
     * Insert trailer
     * This method inserts the new trailer.
     * @param trailer the trailer to be saved
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrailer(trailer: DatabaseTrailer)

    /**
     * Update trailer fuel
     * This method updates the available fuel info of the trailer
     * @param trailerId the id of the trailer to be updated
     * @param fuelQuantity the quantity to be updated
     */
    @Query("update DatabaseTrailer set fuelQuantity =:fuelQuantity where trailerId =:trailerId")
    suspend fun updateTrailerFuel(trailerId: Int, fuelQuantity: Double)

    /**
     * Get trailer reading
     * This method gets the trailer reading
     * @param trailerId the id of the trailer to be read
     * @return the available fuel in the trailer
     */
    @Query("select fuelQuantity from DatabaseTrailer where trailerId =:trailerId")
    fun getTrailerReading(trailerId: Int): LiveData<Int>
}

/**
 * Destination dao
 * Data access object to manipulate destination information.
 * @constructor Create empty Destination dao
 */
@Dao
interface DestinationDao {

    /**
     * Insert destination
     * This method inserts the new destination
     * @param siteOrSource the destination to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDestination(vararg siteOrSource: DatabaseSourceOrSite)

    /**
     * Update delivery status
     * This method updates the delivery status for the given destination
     * @param tripId the id of the trip
     * @param seqNum the sequence number of the destination
     * @param deliveryStatus the status to be updated to
     */
    @Query("update DatabaseSourceOrSite set deliveryStatus = :deliveryStatus where seqNum=:seqNum and tripId =:tripId")
    suspend fun updateDeliveryStatus(tripId: Int, seqNum: Int, deliveryStatus: DeliveryStatusEnum)

    /**
     * Get  destination
     * This method gets the destination from the database
     * @param tripId the id of the trip
     * @param seqNum the sequence number of the destination
     * @return the destination info
     */
    @Query("select * from  DatabaseSourceOrSite  where tripId=:tripId and seqNum=:seqNum limit 1")
    fun getDestination(tripId: Int, seqNum: Int): DatabaseSourceOrSite

    /**
     * Get total destinations
     * This method gives the total number of delivery completed.
     * @return the total number of destinations
     */
    @Query("select count(*) from DatabaseSourceOrSite where deliveryStatus = 2")
    fun getTotalDestinations(): Int

}

/**
 * Trip dao
 * Data access object to manipulate trip information.
 * @constructor Create empty Trip dao
 */
@Dao
interface TripDao {

    /**
     * Change trip status
     * This method updates the status of the trip.
     * @param tripId the id of the trip
     * @param deliveryStatus the status to be updated to
     */
    @Query("update DatabaseTrip set deliveryStatus=:deliveryStatus where tripId= :tripId")
    fun changeTripStatus(tripId: Int, deliveryStatus: DeliveryStatusEnum)

    /**
     * Get completed trips id
     * This method gets the ids of the completed trip
     * @return the ids of the trip
     */
    @Query("select tripId from DatabaseTrip where deliveryStatus = 2")
    fun getCompletedTripsId(): List<Int>

    /**
     * Delete trip by id
     * This method removes the trip of the given id
     * @param ids the id of the trip to be removed
     */
    @Query("DELETE FROM DatabaseTrip WHERE tripId IN (:ids) and deliveryStatus not in (0,2) ")
    fun deleteTripById(ids: List<Int>)

    /**
     * Insert trip
     * This method inserts trip information.
     * @param trip the trip to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: DatabaseTrip)

    /**
     * Insert truck
     * This method inserts truck information
     * @param truck the truck info to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTruck(truck: DatabaseTruck)

    /**
     * Get all trip
     * This method gets the list of the trips
     * @return the list of the trips
     */
    @Transaction
    @Query("select * from  DatabaseTrip order by deliveryStatus desc ")
    fun getAllTrip(): LiveData<List<TripWithInfo>>

    /**
     * Get all trips one time
     * This method gets the list of the trip.
     * @return the list of the trip
     */
    @Transaction
    @Query("select * from  DatabaseTrip order by deliveryStatus desc ")
    fun getAllTripsOneTime(): List<TripWithInfo>

    /**
     * Get trip by id
     * This method gets the trip by the given id.
     * @param tripId the id of the trip
     * @return the trip
     */
    @Query("select * from  DatabaseTrip  where tripId=:tripId")
    fun getTripById(tripId: Int): DatabaseTrip?

    /**
     * Get total completed trips
     * This method gets the total number of trips completed.
     * @return the total number of trips completed.
     */
    @Query("select count(*) from DatabaseTrip where deliveryStatus = 2")
    fun getTotalCompletedTrips(): Int

}

/**
 * Products dao
 * Data access object to manipulate product information.
 * @constructor Create empty Products dao
 */
@Dao
interface ProductsDao {

    /**
     * Insert fuel
     * Inserts new fuel to the database
     * @param fuel the fuel to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuel(fuel: DatabaseFuel)

    /**
     * Get products
     * Gets the product list from the database.
     * @return the list of product
     */
    @Query("select * from DatabaseFuel")
    suspend fun getProducts(): List<DatabaseFuel>
}

/**
 * Completed deliveries dao
 * Data access object to manipulate completed deliveries information.
 * @constructor Create empty Completed deliveries dao
 */
@Dao
interface CompletedDeliveriesDao {

    /**
     * Get details
     * Gets detail about the deliveries completed for certain trip.
     * @param tripId the id of the trip
     * @return the detailed information about the trip
     */
    @Transaction
    @Query("select * from DatabaseCompletionForm where tripId =:tripId")
    fun getDetails(tripId: Int): List<CompletedFormWithInfo>

    /**
     * Get details for destination
     * Gets the detail information for the destination.
     * @param tripId the id of the trip
     * @param seqNo the sequence number of destination
     * @return the detail information about the destination
     */
    @Transaction
    @Query("select * from DatabaseCompletionForm where tripId =:tripId and seqNo =:seqNo")
    fun getDetailsForDestination(tripId: Int, seqNo: Int): List<CompletedFormWithInfo>

    /**
     * Get unsent form info
     * Gets the form information from unsent form data
     * @return the information about the form
     */
    @Query(
        "select tripId,billOfLadingNumber,endTime,grossQty,netQty,startTime," +
                "sourceOrSiteId as sourceOrSiteId ,driverId as driverCode,productDelivered as productId," +
                "wayPointType as type,trailerEndReading as trailerRemainingQuantity from DatabaseCompletionForm " +
                "where   dataSent = 0"
    )
    fun getUnsentFormInfo(): List<ProductData>

    /**
     * Update form sent status
     * Updates the status of the from if its sent
     * @param id the id of the destination
     */
    @Query("update DatabaseCompletionForm set dataSent = 1 where  sourceOrSiteId=:id")
    fun updateFormSentStatus(id: Int)
}

/**
 * Status put dao
 * Data access object to manipulate status.
 * @constructor Create empty Status put dao
 */
@Dao
interface StatusPutDao {

    /**
     * Insert put data
     * Inserts status data to the database
     * @param toPut data to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPutData(toPut: DatabaseStatusPut)

    /**
     * Delete put data
     * Delete status data from the database
     * @param toDelete the data to be deleted
     */
    @Delete
    fun deletePutData(toDelete: DatabaseStatusPut)

    /**
     * Get all unsent data
     * Gets all the unsent status data
     * @return the usent data
     */
    @Query("select * from DatabaseStatusPut")
    fun getAllUnsentData(): List<DatabaseStatusPut>
}






