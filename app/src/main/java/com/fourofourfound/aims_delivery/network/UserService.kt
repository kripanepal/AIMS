package com.fourofourfound.aims_delivery.network

import android.util.Log
import com.fourofourfound.aims_delivery.database.entities.DatabaseCompletionForm
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

//server url
/**
 * Base URL
 * The URL of the server
 */
//private const val BASE_URL =  "https://aims-server.herokuapp.com/"
private const val BASE_URL = "https://api.appery.io/"
private const val API_KEY = "f20f8b25-b149-481c-9d2c-41aeb76246ef"


// a retrofit object
var retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()


//interface to add the methods
/**
 * User service
 * Interface to make network calls
 * @constructor Create empty User service
 */
interface UserService {
    @GET("/rest/1/apiexpress/api/DispatcherMobileApp/?apiKey=$API_KEY&Active=true")
    suspend fun validateUser(@Query(value = "Code") driverCode: String): DriverResponse


    @GET("/rest/1/apiexpress/api/DispatcherMobileApp/GetDetailedTripListByDriver/{driverCode}?apiKey=$API_KEY")
    suspend fun getAllTrips(@Path("driverCode") driverCode: String): TripResponse

    @GET("rest/1/apiexpress/api/DispatcherMobileApp/TripStatusPut/{driverCode}/{tripId}/{statusCode}/{statusMessage}/true/{statusDate}?apiKey=$API_KEY")
    suspend fun sendStatusUpdate(
        @Path("driverCode") driverCode: String,
        @Path("tripId") tripId: Int,
        @Path("statusCode") statusCode: String,
        @Path("statusMessage") statusMessage: String,
        @Path("statusDate") statusDate: String, ): StatusMessageUpdateResponse


    @GET("/alltrips")
    suspend fun getAllTripsFromHeroku(): TripResponse



    @POST("/location")
    suspend fun sendLocation(@Body location: CustomDatabaseLocation)


    @POST("/form")
    fun sendFormData(@Body form: DatabaseCompletionForm)


    @GET("/rest/1/apiexpress/api/DispatcherMobileApp/TripProductPickupPut/{driverCode}/{tripId}/{sourceId}/{productId}/{bol}/{startDate}/{endDate}/{grossQuantity}/{netQuantity}?apiKey=f20f8b25-b149-481c-9d2c-41aeb76246ef")
    suspend  fun sendProductPickupInfo( @Path("driverCode") driverCode: String,
                               @Path("tripId") tripId: Int,
                               @Path("sourceId") sourceId: Int,
                               @Path("productId") productId: Int,
                               @Path("bol") bol: String,
                               @Path("startDate") startDate: String,
                               @Path("endDate") endDate: String,
                               @Path("grossQuantity") grossQuantity: Int,
                               @Path("netQuantity") netQuantity: Int,
                               ): ProductPickupResponse


}

/**
 * Make network call
 * Object to link interface and retrofit object
 * @constructor Create empty Make network call
 */
object MakeNetworkCall {
    val retrofitService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }
}