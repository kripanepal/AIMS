package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import com.google.android.material.circularreveal.CircularRevealHelper

@Entity
data class Driver(
    var driver_id: String,
    var driver_name: String,
    @PrimaryKey
    var id: Int = 1
)