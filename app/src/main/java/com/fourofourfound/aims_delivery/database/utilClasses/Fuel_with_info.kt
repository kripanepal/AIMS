package com.fourofourfound.aims_delivery.database.utilClasses

data class Fuel_with_info(
    var fuel_type: String,
    var fuel_source: String = "0",
    var site_count: String
)