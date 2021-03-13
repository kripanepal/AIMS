package com.fourofourfound.aims_delivery.database.entities.site

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class Site(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    var name: String
)