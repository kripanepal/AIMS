package com.fourofourfound.aims_delivery.utils

import android.content.Context
import com.fourofourfound.aims_delivery.CustomSharedPreferences
import com.fourofourfound.aims_delivery.database.TripListDatabase
import com.fourofourfound.aims_delivery.database.getDatabase

fun getDatabaseForDriver(context: Context): TripListDatabase {
    var code = ""
    CustomSharedPreferences(context).apply {
        code = getEncryptedPreference("driverCode")
    }

    //get the instance of the database
    return getDatabase(context, code)
}

