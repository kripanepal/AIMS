package com.fourofourfound.aims_delivery.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Load
 * This class is used by the application as domain model to store load information
 *
 * @property _id the  unique id of the load
 * @property _v need to remove this
 * @property productType type of product that is being delivered
 * @property productDestination destination of the load/ where it needs to be delivered
 * @property productQuantity quantity of the fuel
 * @property completed status of the delivery
 * @constructor Create empty Load
 */
@Parcelize
data class Load(
    val _id: String,
    val _v: Int = 0,

    val productType: String,
    val productDestination:String,
    val productQuantity:Double,
    val completed: Boolean =false

): Parcelable