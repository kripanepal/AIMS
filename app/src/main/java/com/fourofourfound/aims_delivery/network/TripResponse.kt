package com.fourofourfound.aims_delivery.network

import com.google.gson.annotations.SerializedName

/**
 * Trip response
 * This class represents the response of the restful server for the
 * list of trips.
 * @constructor
 */
class TripResponse {

    /**
     * Data
     * The content of the the response.
     */
    lateinit var data: Data

    /**
     * Status
     * The status of the response.
     */
    lateinit var status: String

    /**
     * Data
     * The class represents list of the trips.
     * @constructor
     */
    class Data {

        /**
         * Result set1
         * The list of network trip.
         */
        lateinit var resultSet1: List<NetworkTrip>
    }
}

/**
 * Driver response
 * This class represents the response of the restful server for the
 * driver information.
 * @constructor
 */
class DriverResponse {

    /**
     * Data
     * The content of the the response.
     */
    lateinit var data: Data

    /**
     * Status
     * The status of the response.
     */
    lateinit var status: String

    /**
     * Data
     * The class represents list of driver information.
     * @constructor
     */
    class Data {

        /**
         * Result set1
         * The list of driver information.
         */
        lateinit var resultSet1: List<Driver>
    }
}

/**
 * Status message update response
 * This class represents the response of the restful server for the
 * status message.
 * @constructor
 */
class StatusMessageUpdateResponse {

    /**
     * Data
     * The content of the the response.
     */
    lateinit var data: Data

    /**
     * Status
     * The status of the response.
     */
    lateinit var status: String

    /**
     * Data
     * The class represents list of status messages.
     * @constructor
     */
    class Data {

        /**
         * Result set1
         * The list of status information.
         */
        lateinit var resultSet1: List<StatusData>
    }
}

/**
 * Product pickup response
 * This class represents the response of the restful server for the
 * product picked up messages.
 * @constructor
 */
class ProductPickupResponse {
    /**
     * Data
     * The content of the the response.
     */
    lateinit var data: Data

    /**
     * Status
     * The status of the response.
     */
    lateinit var status: String

    /**
     * Data
     * The class represents list of product picked up messages.
     * @constructor
     */
    class Data {

        /**
         * Result set1
         * The list of status data.
         */
        lateinit var resultSet1: List<StatusData>

        /**
         * Result set1
         * The list of product picked up messages.
         */
        lateinit var resultSet2: List<ProductPickupResponseData>
    }

    /**
     * Product pickup response data
     * This class contains the information about the response from product picked up.
     * @property driverCode the code of the driver
     * @property tripId the id of the trip
     * @property sourceId the id of the source
     * @property productId the id of the product
     * @property bol the bill of lading number
     * @property startDate the start date of the delivery
     * @property endDate the end date of the delivery
     * @property grossQuantity the gross quantity of the fuel
     * @property netQuantity the net quantity of the fuel
     * @property tripWayPointID the id of the trip waypoint
     * @property id the id of the destination
     * @constructor
     */
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
        val tripWayPointID: Int,
        @SerializedName("id")
        val id: Int,
    )
}

/**
 * Status data
 * This class represents the response from the restful server
 * @constructor
 */
class StatusData {

    /**
     * Status code
     * The status code received.
     */
    @SerializedName("StatusCode")
    var statusCode: Int = 0

    /**
     * Status
     * The status message received.
     */
    @SerializedName("Status")
    lateinit var status: String

}

