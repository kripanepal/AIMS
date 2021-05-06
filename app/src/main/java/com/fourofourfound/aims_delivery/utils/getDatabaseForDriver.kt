package com.fourofourfound.aims_delivery.utils

import android.content.Context
import com.fourofourfound.aims_delivery.database.TripListDatabase
import com.fourofourfound.aims_delivery.database.getDatabase

/**
 * Get database for driver
 * This method gets the database for the currently logged in driver
 * @param context the context of the application
 * @return the database for the driver
 */
fun getDatabaseForDriver(context: Context): TripListDatabase {
    var code: String
    CustomSharedPreferences(context).apply {
        code = getEncryptedPreference("driverCode")
    }
    //get the instance of the database
    return getDatabase(context, code)
}

