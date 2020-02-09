package com.androidapp.entity.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NodeLocation(
    val locationId: String,
    val locationName: String,
    val slot: String

) : Parcelable
