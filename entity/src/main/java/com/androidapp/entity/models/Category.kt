package com.androidapp.entity.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    @SerializedName("id") val id: String?,
    @SerializedName("slug") val slug: String?,
    @SerializedName("active") val active: Boolean,
    @SerializedName("picture_url") val picture_url: String?,
    @SerializedName("icon_url") val icon_url: String?,
    @SerializedName("weight") val weight: Int?,
    @SerializedName("name") val name: String?
) : Parcelable {
    constructor(name: String) : this(name, "", false, "", "", 0, "")
}
