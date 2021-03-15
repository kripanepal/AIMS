package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class Source(
    @PrimaryKey(autoGenerate = false)
    var sourceId: String,
    var name: String,
    var tripId: String
)