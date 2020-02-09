package com.androidapp.entity.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Coord(
    val x: Int,
    val y: Int
):Parcelable