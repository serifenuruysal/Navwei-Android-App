package com.androidapp.entity.models

data class FloorMap(
    val data: MapData,
    val picture_url: String,
    val svg: String,
    var isCurrent: Boolean = false
)
