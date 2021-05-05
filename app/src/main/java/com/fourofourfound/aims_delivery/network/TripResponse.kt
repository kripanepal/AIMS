package com.fourofourfound.aims_delivery.network

import com.google.gson.annotations.SerializedName

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


class StatusMessageUpdateResponse {
    lateinit var data: Data
    lateinit var status:String

    class Data {
        lateinit var resultSet1: List<StatusData>
    }
}


class ProductPickupResponse {
    lateinit var data: Data
    lateinit var status:String

    class Data {
        lateinit var resultSet1: List<StatusData>
        lateinit var resultSet2: List<ProductPickupResponseData>
    }

    data class ProductPickupResponseData
        (
        @SerializedName("DriverCode")
        val driverCode: String,
        @SerializedName("TripID")
        val tripId: Int,
        @SerializedName("SourceID")
        val sourceId: Int,
        @SerializedName("Productid")
        val productId: Int,
        @SerializedName("ManifestNumber")
        val bol: String,
        @SerializedName("LoadstartDateTime")
        val startDate: String,
        @SerializedName("LoadEndDateTime")
        val endDate: String,
        @SerializedName("SourceGrossQuantity")
        val grossQuantity: Int,
        @SerializedName("SourceNetQuantity")
        val netQuantity: Int,


        @SerializedName("TripWayPointID")
        val tripWayPointID: Int ,

        @SerializedName("id")
        val id: Int,
)

}

class StatusData
{
    @SerializedName("StatusCode")
     var statusCode:Int = 0

    @SerializedName("Status")
    lateinit var status:String

}

