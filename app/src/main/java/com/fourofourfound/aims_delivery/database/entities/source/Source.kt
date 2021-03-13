package com.fourofourfound.aims_delivery.database.entities.source

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class Source(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    var name: String
)