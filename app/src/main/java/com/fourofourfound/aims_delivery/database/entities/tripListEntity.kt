package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.fourofourfound.aims_delivery.domain.Trip

@Entity
data class DatabaseTrip(
    @PrimaryKey
    val _id: String,
    val name: String,
    val _v: Int = 0,
    val completed: Boolean =false
)


fun List<DatabaseTrip>.asDomainModel(): List<Trip> {
    return map {
        Trip(
            _id = it._id,
            name = it.name,
            _v = it._v,
            completed = it.completed

        )
    }
}