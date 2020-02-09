package com.androidapp.entity.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationParentDetails (
    @SerializedName("id") val id : String?,
    @SerializedName("name") val name : String?,
    @SerializedName("short_name") val short_name : String?
):Parcelable
