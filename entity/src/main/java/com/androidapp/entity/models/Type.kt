package com.androidapp.entity.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Type(
    val active: Boolean,
    val icon_url: String?,
    val id: String?,
    val name: String?,
    val picture_url: String?,
    val slug: String?
) : Parcelable
