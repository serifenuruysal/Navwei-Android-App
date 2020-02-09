package com.androidapp.entity.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Country(
    val country_code: String?,
    val id: String?,
    val name: String
) : Parcelable {
    constructor(country_name: String) : this(name = country_name, country_code = null, id = null)
}