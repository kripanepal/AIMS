package com.fourofourfound.aims_delivery.network

import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


//private const val BASE_URL =  "https://aims-server.herokuapp.com/"
/**
 * B a s e_u r l
 * Base url for the restful api server.
 */
private const val BASE_URL = "https://api.appery.io/"

/**
 * A p i_k e y
 * The key for the restful api.
 */
private const val API_KEY = "f20f8b25-b149-481c-9d2c-41aeb76246ef"

/**
 * Retrofit
 * This is a retrofit object to make network calls.
 */
var retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

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

    @GET("/rest/1/apiexpress/api/DispatcherMobileApp/TripProductDeliveryInsert/{driverCode}/{tripId}/{siteId}/{productId}/{deliveryDate}/{grossQuantity}/{netQuantity}/{remainingQuantity}?apiKey=f20f8b25-b149-481c-9d2c-41aeb76246ef")
    suspend  fun sendProductDropOffInfo( @Path("driverCode") driverCode: String,
                               @Path("tripId") tripId: Int,
                               @Path("siteId") sourceId: Int,
                               @Path("productId") productId: Int,
                               @Path("deliveryDate") startDate: String,
                               @Path("grossQuantity") grossQuantity: String,
                               @Path("netQuantity") netQuantity: String,
                               @Path("remainingQuantity") remainingQuantity: String,
                               ): StatusMessageUpdateResponse
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