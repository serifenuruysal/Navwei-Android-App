package com.androidapp.entity.models

import com.google.gson.annotations.SerializedName

data class Slider(
    @SerializedName("id") val id: String,
    @SerializedName("active") val active: Boolean,
    @SerializedName("title") val title: String,
    @SerializedName("items") val items: List<SliderItem>
)
