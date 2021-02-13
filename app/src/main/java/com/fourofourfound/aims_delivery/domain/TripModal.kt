/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.fourofourfound.aims_delivery.domain

import android.os.Parcelable
import com.fourofourfound.aims_delivery.database.TripListDatabse
import com.fourofourfound.aims_delivery.database.entities.DatabaseTrip
import kotlinx.android.parcel.Parcelize

// these are the data objects used and manipulated by the app
@Parcelize
data class Trip(
    val _id: String,
    val name: String, val _v: Int = 0,
    val completed:Boolean
): Parcelable