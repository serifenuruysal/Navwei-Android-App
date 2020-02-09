package com.androidapp.entity.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationDetails (
	@SerializedName("phone") val phone : String?,
	@SerializedName("email") val email : String?,
	@SerializedName("schedule") val schedule : Schedule?,
	@SerializedName("website") val website : String?,
	@SerializedName("logo_url") val logo_url : String?,
	@SerializedName("picture_url") val picture_url : String?,
	@SerializedName("size") val size : Int?,
	@SerializedName("details") val details : Details?,
	@SerializedName("short_name") val short_name : String?,
	@SerializedName("description") val description : String?,
	@SerializedName("keywords") val keywordswords : String?,
	@SerializedName("name") val name : String?
):Parcelable
