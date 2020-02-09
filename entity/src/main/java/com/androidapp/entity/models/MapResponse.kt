package com.androidapp.entity.models

import com.google.gson.annotations.SerializedName

data class MapResponse(

    @field:SerializedName("location_map")
    val floorMap: FloorMap? = null,

    @field:SerializedName("message")
    val message: String? = null
)