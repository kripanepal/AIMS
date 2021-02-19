package com.fourofourfound.aims_delivery.domain

import android.os.Parcelable
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Load(
    val _id: String,
    val _v: Int = 0,

    val productType: String,
    val productDestination:String,
    val productQuantity:Double,
    val completed: Boolean =false

): Parcelable