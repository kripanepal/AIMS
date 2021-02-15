package com.example.location

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.function.DoubleUnaryOperator

@Entity
data class LocationRecords(

    var latitude: Double,
    var longitude: Double,
    var person_type: String,
    var comment: String){
    @PrimaryKey(autoGenerate = true)
    var _id: Long = 0
}