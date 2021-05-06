package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity

/**
 * Bill of lading images
 * This class represents the information about the bill of lading images saved in database
 * for completed deliveries
 * @property identifier the key that identifies the destination
 * @property imagePath the path to the bill of lading image
 * @constructor Create empty Bill of lading images
 */
@Entity(primaryKeys = ["identifier", "imagePath"])
data class BillOfLadingImages(
    var identifier: String,
    var imagePath: String
)