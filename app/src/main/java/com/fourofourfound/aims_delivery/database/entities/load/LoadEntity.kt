package com.fourofourfound.aims_delivery.database.entities.load

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fourofourfound.aims_delivery.domain.Load


@Entity
data class DatabaseLoad(
    @PrimaryKey
    val _id: String,
    val _v: Int = 0,

    val productType: String,
    val productDestination:String,
    val productQuantity:Double,
    val completed: Boolean =false
)


fun List<DatabaseLoad>.asDomainModal(): List<Load> {
    return map {
        Load(
            _id = it._id,
            _v = it._v,
            productType = it.productType,
            productDestination = it.productDestination,
            productQuantity = it.productQuantity,
            completed = it.completed
        )
    }
}

