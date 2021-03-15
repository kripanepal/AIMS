package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class DatabaseSite(
    @PrimaryKey(autoGenerate = false)
    var siteId: String,
    var name: String,
    var tripId: String
)