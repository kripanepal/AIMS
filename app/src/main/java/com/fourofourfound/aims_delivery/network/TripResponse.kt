package com.fourofourfound.aims_delivery.network

import com.fourofourfound.aims_delivery.database.entities.StatusTable
import com.google.gson.annotations.SerializedName
import kotlin.properties.Delegates

class TripResponse {
    lateinit var data: Data
    lateinit var status:String

    class Data {
        lateinit var resultSet1: List<NetworkTrip>
    }
}

class DriverResponse {
    lateinit var data: Data
    lateinit var status:String

    class Data {
        lateinit var resultSet1: List<Driver>
    }
}

class StatusTableResponse {
    lateinit var data: Data
    lateinit var status:String

    class Data {
        lateinit var resultSet1: List<StatusTable>
    }
}

class StatusMessageUpdateResponse {
    lateinit var data: Data
    lateinit var status:String

    class Data {
        lateinit var resultSet1: List<StatusData>
    }
}

class StatusData()
{
    @SerializedName("StatusCode")
     var statusCode:Int = 0

    @SerializedName("Status")
    lateinit var status:String

}
