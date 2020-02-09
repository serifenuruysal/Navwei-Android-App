package com.androidapp.entity.models

import com.google.gson.annotations.SerializedName

data class SliderItem(
    @SerializedName("id") val id: String,
    @SerializedName("voucher_id") val voucher_id: String?,
    @SerializedName("location_id") val location_id: String?,
    @SerializedName("external_link") val external_link: String?,
    @SerializedName("picture_url") val picture_url: String?
)
