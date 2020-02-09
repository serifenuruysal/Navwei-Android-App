package com.androidapp.entity.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Schedule(

    @SerializedName("mon") val monday: String?,
    @SerializedName("sat-sun") val weekends: String?,
    @SerializedName("tue-fri") val thu_fri: String?
) : Parcelable