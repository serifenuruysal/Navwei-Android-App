package com.androidapp.entity.models

import com.google.gson.annotations.SerializedName

class MapData(
    @SerializedName("element_location")
    val storeLocations: List<NodeLocation>?,
    @SerializedName("elements")
    val nodes: List<Node>?,
    val height: Int,
    val width: Int
)