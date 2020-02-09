package com.androidapp.entity.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class City(
    val city_code: String?,
    val country_code: String?,
    val country_id: String?,
    val country_name: String?,
    val id: String?,
    val name: String
) : Parcelable {
    constructor(city_name: String) : this(
        name = city_name,
        id = null,
        city_code = null,
        country_code = null,
        country_id = null,
        country_name = null
    )
}
