package com.fourofourfound.aims_delivery.database.utilClasses

/**
 * Fuel with info
 * This class represents the fuel information.
 * @property fuel_type the type of the fuel
 * @property fuel_source the source fuel is picked up from
 * @property site_count the total number of site
 * @constructor Create empty Fuel with info
 */
data class FuelWithInfo(
    var fuel_type: String,
    var fuel_source: String,
    var site_count: String
)