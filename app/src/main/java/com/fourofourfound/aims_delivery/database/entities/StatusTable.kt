package com.fourofourfound.aims_delivery.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class StatusTable(
    @PrimaryKey
    @SerializedName("Id") val id: Int,
    @SerializedName("StatusCode") val statusCode: String,
    @SerializedName("StatusMessage") val statusMessage: String
)