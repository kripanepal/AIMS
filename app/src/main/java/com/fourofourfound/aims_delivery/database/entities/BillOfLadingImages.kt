package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity

@Entity(primaryKeys = ["identifier", "imagePath"])
data class BillOfLadingImages(

    var identifier: String,
    var imagePath: String
)