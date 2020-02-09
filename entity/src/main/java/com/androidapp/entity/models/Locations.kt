package com.androidapp.entity.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Locations (
    @SerializedName("id") val id : String?,
    @SerializedName("parent_id") val parent_id : String?,
    @SerializedName("coordinate") val coordinate : Coordinate?,
    @SerializedName("weight") val weight : Int?,
    @SerializedName("featured") val featured : Boolean,
    @SerializedName("active") val active : Boolean,
    @SerializedName("location_map_id") val location_map_id : String?,
    @SerializedName("type") val type : Type?,
    @SerializedName("location_details") val location_details : LocationDetails?,
    @SerializedName("city_code") val city_code : String?,
    @SerializedName("city_name") val city_name : String?,
    @SerializedName("country_code") val country_code : String?,
    @SerializedName("country_name") val country_name : String?,
    @SerializedName("categories") val categories : List<Category>?,
    @SerializedName("nb_store") val nb_store : Int?,
    @SerializedName("location_parent_details")
    val location_parent_details : LocationParentDetails?
): Parcelable
