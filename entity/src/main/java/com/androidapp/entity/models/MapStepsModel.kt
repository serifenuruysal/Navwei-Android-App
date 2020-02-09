package com.androidapp.entity.models

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by S.Nur Uysal on 2019-12-28.
 */

@Parcelize
data class MapStepsModel(val stepName: String, val resource: Int?, val bitmap: Bitmap?,val isBold:Boolean) : Parcelable