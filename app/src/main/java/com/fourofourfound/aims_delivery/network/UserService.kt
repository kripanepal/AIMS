package com.fourofourfound.aims_delivery.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

//server url
private const val BASE_URL = "https://aims-server.herokuapp.com"


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
}

//object to link interface and retrofit object
object MakeNetworkCall{
    val retrofitService : UserService by lazy {
        retrofit.create(UserService::class.java)
    }
}