package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class DatabaseSource(
    @PrimaryKey
    var sourceId: String,
    var name: String,
    var tripId: String
)