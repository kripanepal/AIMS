package com.fourofourfound.aims_delivery.network.user

import android.util.Log
import com.fourofourfound.aims_delivery.database.entities.location.CustomDatabaseLocation
import com.fourofourfound.aims_delivery.domain.UserLoginInfo
import com.fourofourfound.aims_delivery.network.tripList.NetworkTripList
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

//server url
private const val BASE_URL = "https://aims-server.herokuapp.com"


//a moshi object
val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


// a retrofit object
var retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

//interface to add the methods
interface UserService {
    @POST("/signin")
    suspend fun validateUser(@Body loginInfo: UserLoginInfo): Boolean

    @POST("/location")
    suspend fun sendLocation(@Body location: CustomDatabaseLocation) {
        Log.i("Refresh", location.toString())
    }

    @GET("/alltrips")
    suspend fun getAllTrips(): List<NetworkTripList>
}

//object to link interface and retrofit object
object MakeNetworkCall {
    val retrofitService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }
}