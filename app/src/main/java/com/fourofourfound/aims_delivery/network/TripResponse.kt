package com.fourofourfound.aims_delivery.network

class TripResponse {
    lateinit var data: Data
}

class Data {
    lateinit var resultSet1: List<NetworkModels.NetworkTrip>
}
